package com.fixadate.domain.googleCalendar.service;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.exception.AdateIOException;
import com.fixadate.domain.adate.service.AdateService;
import com.fixadate.domain.googleCalendar.entity.GoogleCredentials;
import com.fixadate.domain.googleCalendar.exception.GoogleCredentialsNotFoundException;
import com.fixadate.domain.googleCalendar.repository.GoogleRepository;
import com.fixadate.global.util.GoogleUtils;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Channel;
import com.google.api.services.calendar.model.Event;
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
    private final GoogleUtils googleUtils;
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
            Calendar.Events.List request = googleUtils.calendarService(userId).events().list(CALENDAR_ID)
                    .setOauthToken(googleCredentials.getAccessToken());
            String syncToken = getNextSyncToken();
            List<Event> events;
            if (syncToken == null) {
                log.info("syncToken 없다");
                events = request.execute().getItems();
            } else {
                log.info("syncToken 있다");
                request.setSyncToken(syncToken);
                events = request.execute().getItems();
                googleUtils.getSyncSettingsDataStore().set(SYNC_TOKEN_KEY, request.getSyncToken());
            }
            syncEvents(events);
        } catch (IOException e) {
            throw new AdateIOException(e);
        }
    }

    //todo : 변경 / 삭제 안됨
    public void syncEvents(List<Event> events) throws IOException {
        List<Event> useLessEvents = new ArrayList<>();
        List<Adate> eventsToRemove = new ArrayList<>();

        for (Event event : events) {
            log.info(event.toPrettyString());
            log.info("여기 봐!!");
            Optional<Adate> adateOptional = adateService.getAdateFromRepository(event.getId());
            if (adateOptional.isPresent()) {
                Adate adate = adateOptional.get();
                log.info("1");

                if (event.getStatus().equals(CALENDAR_CANCELLED)) {
                    log.info("2");
                    eventsToRemove.add(adate);
                    useLessEvents.add(event);
                    continue;
                }
                if (adate.getEtag().equals(event.getEtag())) {
                    log.info("3");
                    useLessEvents.add(event);
                } else {
                    log.info("4");
                    adate.updateFrom(event);
                    useLessEvents.add(event);
                }
            }
        }
        log.info("5");

        events.removeAll(useLessEvents);
        adateService.removeEvents(eventsToRemove);
        adateService.registEvents(events);
    }

    public Channel executeWatchRequest(String userId) {
        return googleUtils.executeWatchRequest(userId);
    }

    public String getNextSyncToken() {
        try {
            return googleUtils.getSyncSettingsDataStore().get(SYNC_TOKEN_KEY);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void registGoogleCredentials(Channel channel, TokenResponse tokenResponse, String userId) {
        //todo : mapper 사용하는 방향으로 리팩토링 하기
        GoogleCredentials googleCredentials = GoogleCredentials
                .getGoogleCredentialsFromCredentials(channel, userId, tokenResponse.getAccessToken());
        googleUtils.registCredentials(tokenResponse, userId);
        googleRepository.save(googleCredentials);
    }
}
