package com.fixadate.domain.googleCalendar.service;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.exception.AdateIOException;
import com.fixadate.domain.adate.service.AdateService;
import com.fixadate.domain.googleCalendar.entity.GoogleCredentials;
import com.fixadate.domain.googleCalendar.exception.GoogleCredentialsNotFoundException;
import com.fixadate.domain.googleCalendar.exception.GoogleNextSyncTokenException;
import com.fixadate.domain.googleCalendar.repository.GoogleRepository;
import com.fixadate.global.config.GoogleApiConfig;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Channel;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleService {
    private final GoogleApiConfig googleApiConfig;
    private final AdateService adateService;
    private final GoogleRepository googleRepository;

    static final String CALENDAR_ID = "primary";
    static final String CALENDAR_CANCELLED = "cancelled";
    private static final String SYNC_TOKEN_KEY = "syncToken";

    @Transactional
    public void listEvents(String channelId) {
        try {
            GoogleCredentials googleCredentials = googleRepository.findGoogleCredentialsByChannelId(channelId)
                    .orElseThrow(GoogleCredentialsNotFoundException::new);
            String userId = googleCredentials.getUserId();
            Calendar.Events.List request = googleApiConfig.calendarService(userId).events().list("primary");
            String syncToken = getNextSyncToken();
            List<Event> events;
            if (syncToken == null) {
                events = request.execute().getItems();
            } else {
                request.setSyncToken(syncToken);
                events = request.execute().getItems();
                googleApiConfig.getSyncSettingsDataStore().set(SYNC_TOKEN_KEY, syncToken);
            }
            syncEvents(events);
        } catch (IOException e) {
            throw new AdateIOException(e);
        }
    }

    public List<Event> getEvents(String accessToken, String sessionId) throws IOException {
        String nextSyncToken = googleApiConfig.getSyncSettingsDataStore().get(SYNC_TOKEN_KEY);
        if (nextSyncToken == null) {
            return getEventsWhenNextSyncTokenIsNull(accessToken, sessionId);
        } else {
            return getEventsWhenNextSyncTokenExists(accessToken, sessionId, nextSyncToken);
        }
    }


    private List<Event> getEventsWhenNextSyncTokenIsNull(String accessToken, String userId) throws IOException {
        Calendar calendarService = googleApiConfig.calendarService(userId);
        Events event = calendarService.events().list(CALENDAR_ID)
                .setOauthToken(accessToken)
                .execute();
        List<Event> events = event.getItems();
        setNextSyncToken(event);
        return events;
    }

    private List<Event> getEventsWhenNextSyncTokenExists(String accessToken, String userId,
                                                         String nextSyncToken) throws IOException {
        Calendar calendarService = googleApiConfig.calendarService(userId);
        Events event = calendarService.events().list(CALENDAR_ID)
                .setSyncToken(nextSyncToken)
                .setOauthToken(accessToken)
                .execute();
        return event.getItems();
    }

    private void setNextSyncToken(Events events) {
        try {
            String nextSyncToken = events.getNextSyncToken();
            if (nextSyncToken != null) {
                googleApiConfig.getSyncSettingsDataStore().set(SYNC_TOKEN_KEY, nextSyncToken);
            }
        } catch (IOException e) {
            throw new GoogleNextSyncTokenException();
        }
    }

    public void syncEvents(List<Event> events) throws IOException {
        List<Event> eventsToRemove = new ArrayList<>();
        for (Event event : events) {
            Optional<Adate> adateOptional = adateService.getAdateFromRepository(event.getId());
            if (event.getStatus().equals(CALENDAR_CANCELLED)) {
                eventsToRemove.add(event);
                adateOptional.ifPresent(adateService::deleteAdate);
                continue;
            }
            if (adateOptional.isPresent()) {
                Adate adate = adateOptional.get();
                if (adate.getEtag().equals(event.getEtag())) {
                    eventsToRemove.add(event);
                } else adate.updateFrom(event);
            }
        }
        events.removeAll(eventsToRemove);
        for (Event event : events) {
            log.info(event.toPrettyString());
        }
        adateService.registEvents(events);
    }

    public Channel executeWatchRequest(String userId) {
        return googleApiConfig.executeWatchRequest(userId);
    }

    public String getNextSyncToken() {
        try {
            return googleApiConfig.getSyncSettingsDataStore().get(SYNC_TOKEN_KEY);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void registGoogleCredentials(Channel channel, String accessToken, String userId) {
        //todo : mapper 사용하는 방향으로 리팩토링 하기
        GoogleCredentials googleCredentials = GoogleCredentials
                .getGoogleCredentialsFromCredentials(channel, accessToken, userId);
        googleRepository.save(googleCredentials);
    }

}
