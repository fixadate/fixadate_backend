package com.fixadate.domain.adate.service;

import static com.fixadate.global.exception.ExceptionCode.*;
import static com.fixadate.global.util.TimeUtil.*;
import static com.fixadate.global.util.constant.ConstantValue.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fixadate.domain.adate.dto.request.AdateRegistRequest;
import com.fixadate.domain.adate.dto.request.AdateUpdateRequest;
import com.fixadate.domain.adate.dto.response.AdateCalendarEventResponse;
import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.repository.AdateQueryRepository;
import com.fixadate.domain.adate.repository.AdateRepository;
import com.fixadate.domain.colortype.entity.ColorType;
import com.fixadate.domain.colortype.repository.ColorTypeRepository;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.exception.badRequest.InvalidTimeException;
import com.fixadate.global.exception.notFound.AdateNotFoundException;
import com.fixadate.global.exception.notFound.ColorTypeNotFoundException;
import com.google.api.services.calendar.model.Event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdateService {
	private final AdateRepository adateRepository;
	private final AdateQueryRepository adateQueryRepository;
	private final ColorTypeRepository colorTypeRepository;

	@Transactional
	public void registAdateEvent(AdateRegistRequest adateRegistRequest, String tagName, Member member) {
		Adate adate = adateRegistRequest.toEntity(member);
		if (tagName != null && !tagName.isEmpty()) {
			setAdateColorType(adate, member, tagName);
		}
		adateRepository.save(adate);
	}

	@Transactional
	public void setAdateColorType(Adate adate, Member member, String tagName) {
		ColorType colorType = colorTypeRepository.findColorTypeByNameAndMember(tagName, member)
			.orElseThrow(() -> new ColorTypeNotFoundException(NOT_FOUND_COLORTYPE_MEMBER_COLOR));
		adate.setColorType(colorType);
	}

	@Transactional
	public void registEvents(List<Event> events, Member member) {
		ColorType colorType = getGoogleCalendarColorTypeFromMember(member);
		List<Adate> adates = events.stream()
			.map(event -> Adate.getAdateFromEvent(event, member, colorType))
			.toList();
		adateRepository.saveAll(adates);
	}

	public ColorType getGoogleCalendarColorTypeFromMember(Member member) {
		return colorTypeRepository.findColorTypeByNameAndMember(GOOGLE_CALENDAR.getValue(), member)
			.orElseThrow(() -> new ColorTypeNotFoundException(NOT_FOUND_COLORTYPE_MEMBER_COLOR));
	}

	@Transactional
	public void removeEvents(List<Adate> adates) {
		adateRepository.deleteAll(adates);
	}

	@Transactional
	public void removeAdateByCalendarId(String calendarId) {
		Adate adate = getAdateByCalendarId(calendarId).
			orElseThrow(() -> new AdateNotFoundException(NOT_FOUND_ADATE_CALENDAR_ID));
		removeAdate(adate);
	}

	@Transactional
	public void removeAdate(Adate adate) {
		adateRepository.delete(adate);
	}

	@Transactional(readOnly = true)
	public Optional<Adate> getAdateByCalendarId(String calendarId) {
		return adateRepository.findAdateByCalendarId(calendarId);
	}

	@Transactional(readOnly = true)
	public List<AdateCalendarEventResponse> getAdateByStartAndEndTime(Member member, LocalDateTime startDateTime,
		LocalDateTime endDateTime) {
		List<Adate> adates = adateQueryRepository.findByDateRange(member, startDateTime, endDateTime);
		return getResponseDto(adates);
	}

	@Transactional(readOnly = true)
	public List<AdateCalendarEventResponse> getAdatesByMonth(int year, int month, Member member) {
		LocalDateTime startTime = getLocalDateTimeFromYearAndMonth(year, month, true);
		LocalDateTime endTime = getLocalDateTimeFromYearAndMonth(year, month, false);
		checkStartAndEndTime(startTime, endTime);

		return getAdateByStartAndEndTime(member, startTime, endTime);
	}

	@Transactional(readOnly = true)
	public List<AdateCalendarEventResponse> getAdatesByWeek(LocalDate firstDay, LocalDate lastDay, Member member) {
		LocalDateTime startTime = getLocalDateTimeFromLocalDate(firstDay, true);
		LocalDateTime endTime = getLocalDateTimeFromLocalDate(lastDay, false);
		checkStartAndEndTime(startTime, endTime);

		return getAdateByStartAndEndTime(member, startTime, endTime);
	}

	private void checkStartAndEndTime(LocalDateTime startTime, LocalDateTime endTime) {
		if (startTime.isAfter(endTime)) {
			throw new InvalidTimeException(INVALID_START_END_TIME);
		}
	}

	@Transactional
	public AdateCalendarEventResponse updateAdate(String calendarId, AdateUpdateRequest adateUpdateRequest,
		Member member) {
		Adate adate = getAdateByCalendarId(calendarId).orElseThrow(
			() -> new AdateNotFoundException(NOT_FOUND_ADATE_CALENDAR_ID));

		adate.updateAdate(adateUpdateRequest);
		setAdateColorType(adate, member, adateUpdateRequest.colorTypeName());
		return AdateCalendarEventResponse.of(adate);
	}

	private List<AdateCalendarEventResponse> getResponseDto(List<Adate> adates) {
		return adates.stream()
			.map(AdateCalendarEventResponse::of)
			.toList();
	}
}
