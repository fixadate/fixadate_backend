package com.fixadate.domain.adate.service;

import com.fixadate.global.config.GoogleApiConfig;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdateService {
    private final GoogleApiConfig googleApiConfig;

    public List<Event> listEvents(DefaultOAuth2AccessToken oauth2AccessToken) throws IOException, GeneralSecurityException {
        Calendar calendarService = googleApiConfig.calendarService(googleApiConfig.googleAuthorizationCodeFlow());

        Events events = calendarService.events().list("primary")
                .setMaxResults(10)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .setOauthToken(oauth2AccessToken.getValue())
                .execute();

        return events.getItems();
    }
}
