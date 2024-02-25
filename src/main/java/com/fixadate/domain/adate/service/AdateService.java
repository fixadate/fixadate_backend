package com.fixadate.domain.adate.service;

import com.fixadate.domain.adate.dto.request.AdateRegistRequest;
import com.fixadate.domain.adate.dto.request.GoogleCalendarRegistRequest;
import com.fixadate.domain.adate.dto.request.GoogleCalendarTimeRequest;
import com.fixadate.domain.adate.dto.response.AdateCalendarEventResponse;
import com.fixadate.domain.adate.dto.response.GoogleCalendarEventResponse;
import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.exception.EventNotExistException;
import com.fixadate.domain.adate.repository.AdateQueryRepository;
import com.fixadate.domain.adate.repository.AdateRepository;
import com.fixadate.domain.colortype.entity.ColorType;
import com.fixadate.domain.colortype.exception.ColorTypeNotFoundException;
import com.fixadate.domain.colortype.repository.ColorTypeRepository;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.config.GoogleApiConfig;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdateService {
    private final GoogleApiConfig googleApiConfig;
    private final AdateRepository adateRepository;
    private final AdateQueryRepository adateQueryRepository;
    private final ColorTypeRepository colorTypeRepository;
    public static final String calendarId = "primary";
    public static final String calendarOrderBy = "startTime";
    public static final String calendarCancelStatus = "cancelled";

    public List<GoogleCalendarEventResponse> listEvents(DefaultOAuth2AccessToken oauth2AccessToken,
                                                        GoogleCalendarTimeRequest googleCalendarTimeRequest)
            throws IOException, GeneralSecurityException, ParseException {
        Calendar calendarService = googleApiConfig.calendarService(googleApiConfig.googleAuthorizationCodeFlow());

        Events events = calendarService.events().list(calendarId)
                .setMaxResults(googleCalendarTimeRequest.range())
                .setOrderBy(calendarOrderBy)
                .setShowDeleted(false)
                .setTimeMax(googleCalendarTimeRequest.getDateTimes().get(0))
                .setTimeMin(googleCalendarTimeRequest.getDateTimes().get(1))
                .setSingleEvents(true)
                .setOauthToken(oauth2AccessToken.getValue())
                .setShowDeleted(true)
                .execute();

        return getEventResponse(events.getItems());
    }

    public List<GoogleCalendarEventResponse> getEventResponse(List<Event> events) {
        return events.stream()
                .map(GoogleCalendarEventResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public void registGoogleEvent(List<GoogleCalendarRegistRequest> googleCalendarRegistRequests, Member member) {
        for (GoogleCalendarRegistRequest googleCalendarRegistRequest : googleCalendarRegistRequests) {
            String calendarId = googleCalendarRegistRequest.calendarId();

            if (calendarCancelStatus.equals(googleCalendarRegistRequest.status())) {
                deleteAdateIfExists(calendarId);
            } else {
                processAdate(googleCalendarRegistRequest, calendarId, member);
            }
        }
    }

    private void deleteAdateIfExists(String calendarId) {
        adateRepository.delete(getAdateFromRepository(calendarId));
    }

    @Transactional
    public void processAdate(GoogleCalendarRegistRequest googleCalendarRegistRequest, String calendarId, Member member) {
        if (checkCalendarIdExists(calendarId)) {
            Adate adate = getAdateFromRepository(calendarId);
            if (!adate.getVersion().equals(googleCalendarRegistRequest.version())) {
                adate.updateFrom(googleCalendarRegistRequest);
                setAdateColorType(adate);
            }
        } else {
            Adate adate = googleCalendarRegistRequest.toEntity(member);
            setAdateColorType(adate);
            adateRepository.save(adate);
        }
    }

    private boolean checkCalendarIdExists(String calendarId) {
        return adateRepository.findAdateByCalendarId(calendarId).isPresent();
    }

    private Adate getAdateFromRepository(String calendarId) {
        return adateRepository.findAdateByCalendarId(calendarId)
                .orElseThrow(EventNotExistException::new);
    }

    @Transactional
    public void registAdateEvent(AdateRegistRequest adateRegistRequest, Member member) {
        Adate adate = adateRegistRequest.toEntity(member);
        setAdateColorType(adate);
        adateRepository.save(adate);
    }

    public void setAdateColorType(Adate adate) {
        ColorType colorType = colorTypeRepository.findColorTypeByColor(adate.getColor())
                .orElseThrow(ColorTypeNotFoundException::new);
        adate.setColorType(colorType);
    }

    public List<AdateCalendarEventResponse> getAdateCalendarEvents(Member member, LocalDateTime startDateTime,
                                                                   LocalDateTime endDateTime) {
        List<Adate> adates = adateQueryRepository.findByDateRange(member, startDateTime, endDateTime);
        return getResponseDto(adates);
    }

    private List<AdateCalendarEventResponse> getResponseDto(List<Adate> adates) {
        return adates.stream()
                .map(AdateCalendarEventResponse::of)
                .collect(Collectors.toList());
    }

    public List<Adate> getAdateResponseByMemberName(String memberName) {
        return adateQueryRepository.findAdatesByMemberName(memberName);
    }
}
