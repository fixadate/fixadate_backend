package com.fixadate.domain.adate.service;

import com.fixadate.domain.adate.dto.request.AdateRegistRequest;
import com.fixadate.domain.adate.dto.response.AdateCalendarEventResponse;
import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.repository.AdateQueryRepository;
import com.fixadate.domain.adate.repository.AdateRepository;
import com.fixadate.domain.colortype.entity.ColorType;
import com.fixadate.domain.colortype.exception.ColorTypeNotFoundException;
import com.fixadate.domain.colortype.repository.ColorTypeRepository;
import com.fixadate.domain.member.entity.Member;
import com.google.api.services.calendar.model.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.fixadate.global.util.DateTimeParseUtils.getLocalDateTimeFromLocalDate;
import static com.fixadate.global.util.DateTimeParseUtils.getLocalDateTimeFromYearAndMonth;


@Service
@RequiredArgsConstructor
@Slf4j
public class AdateService {
    private final AdateRepository adateRepository;
    private final AdateQueryRepository adateQueryRepository;
    private final ColorTypeRepository colorTypeRepository;

    @Transactional
    public void registAdateEvent(AdateRegistRequest adateRegistRequest, Member member) {
        Adate adate = adateRegistRequest.toEntity(member);
        setAdateColorType(adate);
        adateRepository.save(adate);
    }

    @Transactional
    public void setAdateColorType(Adate adate) {
        ColorType colorType = colorTypeRepository.findColorTypeByColor(adate.getColor())
                .orElseThrow(ColorTypeNotFoundException::new);
        adate.setColorType(colorType);
    }

    @Transactional(readOnly = true)
    public List<AdateCalendarEventResponse> getAdateResponseByMemberName(String memberName) {
        return getResponseDto(adateQueryRepository.findAdatesByMemberName(memberName));
    }

    @Transactional
    public void registEvents(List<Event> events) {
        List<Adate> adates = events.stream()
                .map(Adate::getAdateFromEvent)
                .toList();
        adateRepository.saveAll(adates);
    }

    @Transactional
    public void removeEvents(List<Adate> adates) {
        adateRepository.deleteAll(adates);
    }

    public void deleteAdate(Adate adate) {
        adateRepository.delete(adate);
    }

    @Transactional(readOnly = true)
    public Optional<Adate> getAdateFromRepository(String calendarId) {
        return adateRepository.findAdateByCalendarId(calendarId);
    }

    @Transactional(readOnly = true)
    public List<AdateCalendarEventResponse> getAdateCalendarEvents(Member member, LocalDateTime startDateTime,
                                                                   LocalDateTime endDateTime) {
        List<Adate> adates = adateQueryRepository.findByDateRange(member, startDateTime, endDateTime);
        return getResponseDto(adates);
    }

    @Transactional(readOnly = true)
    public List<AdateCalendarEventResponse> getAdatesByMonth(int year, int month, Member member) {
        LocalDateTime startTime = getLocalDateTimeFromYearAndMonth(year, month, true);
        LocalDateTime endTime = getLocalDateTimeFromYearAndMonth(year, month, false);

        return getAdateCalendarEvents(member, startTime, endTime);
    }



    @Transactional(readOnly = true)
    public List<AdateCalendarEventResponse> getAdatesByWeek(LocalDate firstDay, LocalDate lastDay, Member member) {
        LocalDateTime startTime = getLocalDateTimeFromLocalDate(firstDay, true);
        LocalDateTime endTime = getLocalDateTimeFromLocalDate(lastDay, false);

        return getAdateCalendarEvents(member, startTime, endTime);
    }

    private List<AdateCalendarEventResponse> getResponseDto(List<Adate> adates) {
        return adates.stream()
                .map(AdateCalendarEventResponse::of)
                .toList();
    }
}
