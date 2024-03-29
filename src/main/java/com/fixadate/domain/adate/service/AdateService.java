package com.fixadate.domain.adate.service;

import com.fixadate.domain.adate.dto.request.AdateRegistRequest;
import com.fixadate.domain.adate.dto.response.AdateCalendarEventResponse;
import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.exception.AdateIOException;
import com.fixadate.domain.adate.exception.GoogleNextSyncTokenException;
import com.fixadate.domain.adate.repository.AdateQueryRepository;
import com.fixadate.domain.adate.repository.AdateRepository;
import com.fixadate.domain.colortype.entity.ColorType;
import com.fixadate.domain.colortype.exception.ColorTypeNotFoundException;
import com.fixadate.domain.colortype.repository.ColorTypeRepository;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.service.MemberService;
import com.fixadate.global.config.GoogleApiConfig;
import com.google.api.client.util.store.DataStore;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Channel;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdateService {
    private final GoogleApiConfig googleApiConfig;
    private final AdateRepository adateRepository;
    private final AdateQueryRepository adateQueryRepository;
    private final ColorTypeRepository colorTypeRepository;
    private final MemberService memberService;
    private DataStore<String> syncSettingsDataStore;

    static final String CALENDAR_ID = "primary";
    static final String CALENDAR_CANCELLED = "cancelled";

    @Transactional
    public void listEvents(String accessToken, String sessionId, Long memberId) {
        try {
            List<Event> events = getEvents(accessToken, sessionId);
            syncEvents(events, memberId);
        } catch (IOException e) {
            throw new AdateIOException(e);
        }
    }

    public List<Event> getEvents(String accessToken, String sessionId) throws IOException {
        syncSettingsDataStore = GoogleApiConfig.getDataStoreFactory().getDataStore("SyncSettings");
        String nextSyncToken = syncSettingsDataStore.get(sessionId);
        if (nextSyncToken == null) {
            return getEventsWhenNextSyncTokenIsNull(accessToken, sessionId);
        } else {
            return getEventsWhenNextSyncTokenExists(accessToken, sessionId, nextSyncToken);
        }
    }

    public void syncEvents(List<Event> events, Long memberId) {
        List<Event> eventsToRemove = new ArrayList<>();
        for (Event event : events) {
            Optional<Adate> adateOptional = getAdateFromRepository(event.getId());
            if (event.getStatus().equals(CALENDAR_CANCELLED)) {
                eventsToRemove.add(event);
                adateOptional.ifPresent(this::deleteAdate);
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
        registEvents(events, memberId);
    }


    private void deleteAdate(Adate adate) {
        adateRepository.delete(adate);
    }

    private Optional<Adate> getAdateFromRepository(String calendarId) {
        return adateRepository.findAdateByCalendarId(calendarId);
    }

    public void registEvents(List<Event> events, Long memberId) {
        Member member = memberService.getMemberFromId(memberId);
        List<Adate> adates = events.stream()
                .map((Event event) -> Adate.getAdateFromEvent(event, member))
                .toList();
        adateRepository.saveAll(adates);
    }


    private List<Event> getEventsWhenNextSyncTokenIsNull(String accessToken, String userId) throws IOException {
        Calendar calendarService = googleApiConfig.calendarService(userId);
        Events event = calendarService.events().list(CALENDAR_ID)
                .setOauthToken(accessToken)
                .execute();
        List<Event> events = event.getItems();
        setNextSyncToken(userId, event);
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

    private void setNextSyncToken(String userId, Events events) {
        try {
            String nextSyncToken = events.getNextSyncToken();
            if (nextSyncToken != null) {
                syncSettingsDataStore.set(userId, nextSyncToken);
            }
        } catch (IOException e) {
            throw new GoogleNextSyncTokenException();
        }
    }

    @Transactional
    public void registAdateEvent(AdateRegistRequest adateRegistRequest, Member member) {
        Adate adate = adateRegistRequest.toEntity(member);
        setAdateColorType(adate);
        adateRepository.save(adate);
    }

    private void setAdateColorType(Adate adate) {
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

    public Channel executeWatchRequest(String userId) {
        return googleApiConfig.executeWatchRequest(userId);
    }

    public String getNextSyncToken(String userId) {
        return googleApiConfig.getNextSyncToken(userId);
    }
}
