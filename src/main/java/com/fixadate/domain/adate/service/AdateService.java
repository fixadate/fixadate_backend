package com.fixadate.domain.adate.service;

import static com.fixadate.domain.adate.mapper.AdateMapper.*;
import static com.fixadate.global.exception.ExceptionCode.*;
import static com.fixadate.global.util.TimeUtil.*;
import static com.fixadate.global.util.constant.ConstantValue.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fixadate.domain.adate.dto.request.AdateRegistRequest;
import com.fixadate.domain.adate.dto.request.AdateUpdateRequest;
import com.fixadate.domain.adate.dto.response.AdateResponse;
import com.fixadate.domain.adate.dto.response.AdateViewResponse;
import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.mapper.AdateMapper;
import com.fixadate.domain.adate.repository.AdateQueryRepository;
import com.fixadate.domain.adate.repository.AdateRepository;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.tag.event.object.TagSettingEvent;
import com.fixadate.global.exception.badRequest.InvalidTimeException;
import com.fixadate.global.exception.notFound.AdateNotFoundException;
import com.fixadate.global.exception.notFound.TagNotFoundException;
import com.fixadate.global.facade.RedisFacade;
import com.google.api.services.calendar.model.Event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdateService {
	private final AdateRepository adateRepository;
	private final AdateQueryRepository adateQueryRepository;
	private final RedisFacade redisFacade;
	private final ApplicationEventPublisher applicationEventPublisher;

	@Transactional(noRollbackFor = TagNotFoundException.class)
	public void registAdateEvent(AdateRegistRequest adateRegistRequest, String tagName, Member member) {
		Adate adate = registDtoToEntity(adateRegistRequest, member);
		adateRepository.save(adate);

		if (tagName != null && !tagName.isEmpty()) {
			applicationEventPublisher.publishEvent(new TagSettingEvent(adate, member, tagName));
		}
	}

	@Transactional
	public AdateResponse restoreAdateByCalendarId(String calendarId) {
		Adate adate = redisFacade.getAndDeleteObjectRedis(ADATE_WITH_COLON.getValue() + calendarId, Adate.class);
		return toAdateResponse(adateRepository.save(adate));
	}

	@Transactional(noRollbackFor = TagNotFoundException.class)
	public void registEvent(Event event, Member member) {
		Adate adate = eventToEntity(event);
		adateRepository.save(adate);

		applicationEventPublisher.publishEvent(new TagSettingEvent(adate, member, GOOGLE_CALENDAR.getValue()));
	}

	@Transactional
	public void removeEvents(List<Adate> adates) {
		adateRepository.deleteAll(adates);
	}

	public void removeAdate(Adate adate) {
		adateRepository.delete(adate);
	}

	@Transactional
	public void removeAdateByCalendarId(String calendarId) {
		Adate adate = getAdateByCalendarId(calendarId).orElseThrow(
			() -> new AdateNotFoundException(NOT_FOUND_ADATE_CALENDAR_ID));

		removeAdate(adate);
		redisFacade.setObjectRedis(ADATE_WITH_COLON.getValue() + calendarId, adate, Duration.ofDays(20));
	}

	@Transactional(readOnly = true)
	public Optional<Adate> getAdateByCalendarId(String calendarId) {
		return adateRepository.findAdateByCalendarId(calendarId);
	}

	@Transactional(readOnly = true)
	public AdateResponse getAdateResponseByCalendarId(String calendarId) {
		Adate adate = getAdateByCalendarId(calendarId).orElseThrow(
			() -> new AdateNotFoundException(NOT_FOUND_ADATE_CALENDAR_ID));
		return getAdateResponse(adate);
	}

	@Transactional(readOnly = true)
	public List<AdateViewResponse> getAdateByStartAndEndTime(Member member, LocalDateTime startDateTime,
		LocalDateTime endDateTime) {
		List<Adate> adates = adateQueryRepository.findByDateRange(member, startDateTime, endDateTime);
		return getResponseDtosFromAdateList(adates);
	}

	@Transactional(readOnly = true)
	public List<AdateViewResponse> getAdatesByMonth(int year, int month, Member member) {
		LocalDateTime startTime = getLocalDateTimeFromYearAndMonth(year, month, true);
		LocalDateTime endTime = getLocalDateTimeFromYearAndMonth(year, month, false);
		checkStartAndEndTime(startTime, endTime);

		return getAdateByStartAndEndTime(member, startTime, endTime);
	}

	@Transactional(readOnly = true)
	public List<AdateViewResponse> getAdatesByWeek(LocalDate firstDay, LocalDate lastDay, Member member) {
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

	@Transactional(noRollbackFor = TagNotFoundException.class)
	public AdateResponse updateAdate(String calendarId, AdateUpdateRequest adateUpdateRequest, Member member) {
		Adate adate = getAdateByCalendarId(calendarId).orElseThrow(
			() -> new AdateNotFoundException(NOT_FOUND_ADATE_CALENDAR_ID));

		adate.updateAdate(adateUpdateRequest);
		adateRepository.save(adate);

		applicationEventPublisher.publishEvent(new TagSettingEvent(adate, member, adateUpdateRequest.tagName()));
		return toAdateResponse(adate);
	}

	private List<AdateViewResponse> getResponseDtosFromAdateList(List<Adate> adates) {
		return adates.stream().map(AdateMapper::toAdateViewResponse).toList();
	}

	private AdateResponse getAdateResponse(Adate adate) {
		return toAdateResponse(adate);
	}
}
