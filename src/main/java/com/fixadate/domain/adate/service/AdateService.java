package com.fixadate.domain.adate.service;

import com.fixadate.domain.adate.dto.request.AdateRegistRequest;
import com.fixadate.domain.adate.dto.request.GoogleCalendarRegistRequest;
import com.fixadate.domain.adate.dto.request.GoogleCalendarTimeRequest;
import com.fixadate.domain.adate.dto.response.AdateCalendarEventResponse;
import com.fixadate.domain.adate.dto.response.GoogleCalendarEventResponse;
import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.exception.AdateIOException;
import com.fixadate.domain.adate.exception.EventNotExistException;
import com.fixadate.domain.adate.repository.AdateQueryRepository;
import com.fixadate.domain.adate.repository.AdateRepository;
import com.fixadate.domain.colortype.entity.ColorType;
import com.fixadate.domain.colortype.exception.ColorTypeNotFoundException;
import com.fixadate.domain.colortype.repository.ColorTypeRepository;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.config.GoogleApiConfig;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Channel;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdateService {
    private final GoogleApiConfig googleApiConfig;
    private final AdateRepository adateRepository;
    private final AdateQueryRepository adateQueryRepository;
    private final ColorTypeRepository colorTypeRepository;
    static final String CALENDAR_ID = "primary";
    static final String CALENDAR_ORDER_BY = "startTime";
    static final String CALENDAR_CANCELLED = "cancelled";
    static final String BASE_WATCH_URL = "https://www.googleapis.com/calendar/v3/calendars/";
    static final String EVENT_WATCH = "/events/watch";
    static final String AUTHORIZATION = "Authorization";
    static final String BEARER = "Bearer ";
    static final String CHANNEL_TYPE = "web_hook";
    static final String BASE_URL = "https://api/fixadate.app";
    static final String NOTIFICATION_URL = "/google/notifications";
    private static HttpTransport HTTP_TRANSPORT;
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static GoogleAuthorizationCodeFlow.Builder builder;


    public List<GoogleCalendarEventResponse> listEvents(DefaultOAuth2AccessToken oauth2AccessToken,
                                                        GoogleCalendarTimeRequest googleCalendarTimeRequest) {
        try {
            Calendar calendarService = googleApiConfig.calendarService();
            Events events = calendarService.events().list(CALENDAR_ID)
                    .setMaxResults(googleCalendarTimeRequest.range())
                    .setOrderBy(CALENDAR_ORDER_BY)
                    .setShowDeleted(false)
                    .setTimeMax(googleCalendarTimeRequest.getDateTimes().get(0))
                    .setTimeMin(googleCalendarTimeRequest.getDateTimes().get(1))
                    .setSingleEvents(true)
                    .setOauthToken(oauth2AccessToken.getValue())
                    .setShowDeleted(true)
                    .execute();

            return getEventResponse(events.getItems());
        } catch (IOException e) {
            throw new AdateIOException(e);
        }
    }

    public List<GoogleCalendarEventResponse> getEventResponse(List<Event> events) {
        return events.stream()
                .map(GoogleCalendarEventResponse::of)
                .toList();
    }

    @Transactional
    public void registGoogleEvent(List<GoogleCalendarRegistRequest> googleCalendarRegistRequests, Member member) {
        for (GoogleCalendarRegistRequest googleCalendarRegistRequest : googleCalendarRegistRequests) {
            String calendarId = googleCalendarRegistRequest.calendarId();

            if (CALENDAR_CANCELLED.equals(googleCalendarRegistRequest.status())) {
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
                .toList();
    }

    public List<Adate> getAdateResponseByMemberName(String memberName) {
        return adateQueryRepository.findAdatesByMemberName(memberName);
    }

    public Channel executeWatchRequest() {
        return googleApiConfig.executeWatchRequest();
    }
}
