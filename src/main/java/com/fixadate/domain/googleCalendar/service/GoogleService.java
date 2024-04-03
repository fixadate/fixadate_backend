package com.fixadate.domain.googleCalendar.service;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.exception.AdateIOException;
import com.fixadate.domain.adate.service.AdateService;
import com.fixadate.domain.googleCalendar.entity.GoogleCredentials;
import com.fixadate.domain.googleCalendar.exception.GoogleCredentialException;
import com.fixadate.domain.googleCalendar.exception.GoogleCredentialsNotFoundException;
import com.fixadate.domain.googleCalendar.repository.GoogleRepository;
import com.fixadate.global.util.GoogleUtils;
import com.google.api.client.auth.oauth2.TokenResponse;
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

import static com.fixadate.domain.googleCalendar.entity.constant.GoogleConstantValue.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleService {
    private final GoogleUtils googleUtils;
    private final AdateService adateService;
    private final GoogleRepository googleRepository;

    @Transactional
    public void listEvents(String channelId) {
        try {
            GoogleCredentials googleCredentials = googleRepository.findGoogleCredentialsByChannelId(channelId)
                    .orElseThrow(GoogleCredentialsNotFoundException::new);

            String userId = googleCredentials.getUserId();
            Calendar.Events.List request = googleUtils.calendarService(userId).events().list(CALENDAR_ID)
                    .setOauthToken(googleCredentials.getAccessToken());
            String syncToken = getNextSyncToken(userId);

            List<Event> events;
            if (syncToken == null) {
                setNextSyncToken(request.execute(), userId);
                events = request.execute().getItems();
            } else {
                request.setSyncToken(syncToken);
                setNextSyncToken(request.execute(), userId);
                events = request.execute().getItems();
            }
            syncEvents(events);
        } catch (IOException e) {
            throw new AdateIOException(e);
        }
    }

    @Transactional
    public void syncEvents(List<Event> events) throws IOException {
        List<Event> useLessEvents = new ArrayList<>();
        List<Adate> eventsToRemove = new ArrayList<>();

        for (Event event : events) {
            Optional<Adate> adateOptional = adateService.getAdateFromRepository(event.getId());
            if (event.getStatus().equals(CALENDAR_CANCELLED)) {
                if (adateOptional.isPresent()) {

                    eventsToRemove.add(adateOptional.get());
                    useLessEvents.add(event);
                } else {
                    useLessEvents.add(event);
                }
                continue;
            }
            if (adateOptional.isPresent()) {
                Adate adate = adateOptional.get();

                if (adate.getEtag().equals(event.getEtag())) {
                    useLessEvents.add(event);
                } else {
                    adate.updateFrom(event);
                    useLessEvents.add(event);
                }
            }
        }

        events.removeAll(useLessEvents);
        adateService.removeEvents(eventsToRemove);
        adateService.registEvents(events);
    }

    public Channel executeWatchRequest(String userId) {
        return googleUtils.executeWatchRequest(userId);
    }

    public String getNextSyncToken(String userId) {
        try {
            return googleUtils.getSyncSettingsDataStore().get(SYNC_TOKEN_KEY + userId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setNextSyncToken(Events events, String userId) {
        try {
            googleUtils.getSyncSettingsDataStore().set(SYNC_TOKEN_KEY + userId, events.getNextSyncToken());
        } catch (IOException e) {
            throw new GoogleCredentialException();
        }
    }

    @Transactional
    public void registGoogleCredentials(Channel channel, TokenResponse tokenResponse, String userId) {
        //todo : mapper 사용하는 방향으로 리팩토링 하기
        GoogleCredentials googleCredentials = GoogleCredentials
                .getGoogleCredentialsFromCredentials(channel, userId, tokenResponse.getAccessToken());
        googleUtils.registCredential(tokenResponse, userId);
        googleRepository.save(googleCredentials);
    }
}
