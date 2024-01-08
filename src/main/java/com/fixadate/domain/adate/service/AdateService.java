package com.fixadate.domain.adate.service;

import com.fixadate.domain.adate.dto.request.GoogleCalendarRegistRequest;
import com.fixadate.domain.adate.dto.request.GoogleCalendarTimeRequest;
import com.fixadate.domain.adate.dto.request.NewAdateRequest;
import com.fixadate.domain.adate.dto.response.GoogleCalendarEventResponse;
import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.repository.AdateRepository;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.config.GoogleApiConfig;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import java.text.ParseException;
import java.util.Optional;
import java.util.stream.Collectors;
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
    private final AdateRepository adateRepository;

    public List<GoogleCalendarEventResponse> listEvents(DefaultOAuth2AccessToken oauth2AccessToken,
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

        return getEventResponse(events.getItems());
    }

    public List<GoogleCalendarEventResponse> getEventResponse(List<Event> events) {
        return events.stream()
                .map(GoogleCalendarEventResponse::of)
                .collect(Collectors.toList());
    }

    public void registAdateEvent(List<GoogleCalendarRegistRequest> googleCalendarRegistRequests, Member member) {

        for (GoogleCalendarRegistRequest googleCalendarRegistRequest : googleCalendarRegistRequests) {
            String calendarId = googleCalendarRegistRequest.getCalendarId();
            if (checkCalendarIdExists(calendarId)) {
                Adate adate = getAdateFromRepository(calendarId).get();
                if (adate.getVersion().equals(googleCalendarRegistRequest.getVersion())) {
                    continue;
                } else {
                    Adate updateAdate = NewAdateRequest.toEntity(adate, googleCalendarRegistRequest);
                    adateRepository.save(updateAdate);
                }
            } else {
                Adate adate = googleCalendarRegistRequest.toEntity(member);
                adateRepository.save(adate);
            }
        }
    }

    private boolean checkCalendarIdExists(String calendarId) {
        return getAdateFromRepository(calendarId).isPresent();
    }

    private Optional<Adate> getAdateFromRepository(String calendarId) {
        return adateRepository.findAdateByCalendarId(calendarId);
    }
}
