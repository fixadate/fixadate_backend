package com.fixadate.domain.adate.service;

import com.fixadate.domain.adate.dto.request.GoogleCalendarTimeRequest;
import com.fixadate.global.config.GoogleApiConfig;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import java.text.ParseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdateService {
    private final GoogleApiConfig googleApiConfig;

    public List<Event> listEvents(DefaultOAuth2AccessToken oauth2AccessToken,
                                  GoogleCalendarTimeRequest googleCalendarTimeRequest)
            throws IOException, GeneralSecurityException, ParseException {
        Calendar calendarService = googleApiConfig.calendarService(googleApiConfig.googleAuthorizationCodeFlow());

        Events events = calendarService.events().list("primary")
                .setMaxResults(googleCalendarTimeRequest.getRange())
                .setOrderBy("startTime")
                .setShowDeleted(false)
                .setTimeMax(googleCalendarTimeRequest.getDateTimes().get(0))
                .setTimeMin(googleCalendarTimeRequest.getDateTimes().get(1))
                .setSingleEvents(true)
                .setOauthToken(oauth2AccessToken.getValue())
                .execute();

        return events.getItems();
    }
}
