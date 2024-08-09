package com.fixadate.domain.adate.service;

import static com.fixadate.domain.adate.mapper.AdateMapper.registerDtoToEntity;
import static com.fixadate.domain.adate.mapper.AdateMapper.toAdateResponse;
import static com.fixadate.global.exception.ExceptionCode.INVALID_START_END_TIME;
import static com.fixadate.global.exception.ExceptionCode.NOT_FOUND_ADATE_CALENDAR_ID;
import static com.fixadate.global.util.TimeUtil.getLocalDateTimeFromLocalDate;
import static com.fixadate.global.util.TimeUtil.getLocalDateTimeFromYearAndMonth;
import static com.fixadate.global.util.constant.ConstantValue.ADATE_WITH_COLON;
import static com.fixadate.global.util.constant.ConstantValue.GOOGLE_CALENDAR;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fixadate.domain.adate.dto.request.AdateRegisterRequest;
import com.fixadate.domain.adate.dto.request.AdateUpdateRequest;
import com.fixadate.domain.adate.dto.response.AdateResponse;
import com.fixadate.domain.adate.dto.response.AdateViewResponse;
import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.mapper.AdateMapper;
import com.fixadate.domain.adate.service.repository.AdateRepository;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.tag.event.object.TagSettingEvent;
import com.fixadate.global.exception.badrequest.InvalidTimeException;
import com.fixadate.global.exception.notfound.AdateNotFoundException;
import com.fixadate.global.exception.notfound.TagNotFoundException;
import com.fixadate.global.facade.RedisFacade;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdateService {

	private final AdateRepository adateRepository;
	private final RedisFacade redisFacade;
	private final ApplicationEventPublisher applicationEventPublisher;

	@Transactional(noRollbackFor = TagNotFoundException.class)
	public void registerAdateEvent(final AdateRegisterRequest adateRegisterRequest, final Member member) {
		Adate adate = registerDtoToEntity(adateRegisterRequest, member);
		adateRepository.save(adate);

		final String tagName = adateRegisterRequest.tagName();
		if (tagName != null && !tagName.isEmpty()) {
			applicationEventPublisher.publishEvent(new TagSettingEvent(adate, tagName));
		}
	}

	@Transactional
	public AdateResponse restoreAdateByCalendarId(final String calendarId) {
		final String key = ADATE_WITH_COLON.getValue() + calendarId;
		final Adate adate = redisFacade.getAndDeleteObjectRedis(key, Adate.class);

		return toAdateResponse(adateRepository.save(adate));
	}

	@Transactional(noRollbackFor = TagNotFoundException.class)
	public void registerEvent(final Adate adate) {
		final Adate saveAdate = adateRepository.save(adate);

		applicationEventPublisher.publishEvent(new TagSettingEvent(saveAdate, GOOGLE_CALENDAR.getValue()));
	}

	public void removeAdate(final Adate adate) {
		adateRepository.delete(adate);
	}

	@Transactional
	public void removeAdateByCalendarId(final String calendarId) {
		final Adate adate = getAdateByCalendarId(calendarId).orElseThrow(
			() -> new AdateNotFoundException(NOT_FOUND_ADATE_CALENDAR_ID)
		);
		adateRepository.delete(adate);

		redisFacade.removeAndRegisterObject(ADATE_WITH_COLON.getValue() + calendarId, adate, Duration.ofDays(20));
	}

	@Transactional(readOnly = true)
	public Optional<Adate> getAdateByCalendarId(final String calendarId) {
		return adateRepository.findAdateByCalendarId(calendarId);
	}

	@Transactional(readOnly = true)
	public AdateResponse getAdateResponseByCalendarId(final String calendarId) {
		final Adate adate = getAdateByCalendarId(calendarId).orElseThrow(
			() -> new AdateNotFoundException(NOT_FOUND_ADATE_CALENDAR_ID)
		);

		return toAdateResponse(adate);
	}

	@Transactional(readOnly = true)
	public List<AdateViewResponse> getAdateByStartAndEndTime(
		final Member member,
		final LocalDateTime startDateTime,
		final LocalDateTime endDateTime
	) {
		final List<Adate> adates = adateRepository.findByDateRange(member, startDateTime, endDateTime);

		return adates.stream()
					 .map(AdateMapper::toAdateViewResponse)
					 .toList();
	}

	@Transactional(readOnly = true)
	public List<AdateViewResponse> getAdatesByMonth(final int year, final int month, final Member member) {
		final LocalDateTime startTime = getLocalDateTimeFromYearAndMonth(year, month, true);
		final LocalDateTime endTime = getLocalDateTimeFromYearAndMonth(year, month, false);
		checkStartAndEndTime(startTime, endTime);

		return getAdateByStartAndEndTime(member, startTime, endTime);
	}

	@Transactional(readOnly = true)
	public List<AdateViewResponse> getAdatesByWeek(
		final LocalDate firstDay,
		final LocalDate lastDay,
		final Member member
	) {
		final LocalDateTime startTime = getLocalDateTimeFromLocalDate(firstDay, true);
		final LocalDateTime endTime = getLocalDateTimeFromLocalDate(lastDay, false);
		checkStartAndEndTime(startTime, endTime);

		return getAdateByStartAndEndTime(member, startTime, endTime);
	}

	private void checkStartAndEndTime(final LocalDateTime startTime, final LocalDateTime endTime) {
		if (startTime.isAfter(endTime)) {
			throw new InvalidTimeException(INVALID_START_END_TIME);
		}
	}

	@Transactional(noRollbackFor = TagNotFoundException.class)
	public AdateResponse updateAdate(
		final String calendarId,
		final AdateUpdateRequest adateUpdateRequest,
		final Member member
	) {
		final Adate adate = getAdateByCalendarId(calendarId).orElseThrow(
			() -> new AdateNotFoundException(NOT_FOUND_ADATE_CALENDAR_ID)
		);

		validateUserCanUpdate(adate, member);
		updateIfNotNull(adate, adateUpdateRequest);

		return toAdateResponse(adate);
	}

	private void validateUserCanUpdate(final Adate adate, final Member member) {
		if (adate.isOwner(member)) {

		}
	}

	private void updateIfNotNull(final Adate adate, final AdateUpdateRequest adateUpdateRequest) {
		if (adateUpdateRequest.tagName() != null) {
			applicationEventPublisher.publishEvent(new TagSettingEvent(adate, adateUpdateRequest.tagName()));
		}
		if (adateUpdateRequest.title() != null) {
			adate.updateTitle(adateUpdateRequest.title());
		}
		if (adateUpdateRequest.notes() != null) {
			adate.updateNotes(adateUpdateRequest.notes());
		}
		if (adateUpdateRequest.location() != null) {
			adate.updateLocation(adateUpdateRequest.location());
		}
		if (adateUpdateRequest.alertWhen() != null) {
			adate.updateAlertWhen(adateUpdateRequest.alertWhen());
		}
		if (adateUpdateRequest.repeatFreq() != null) {
			adate.updateRepeatFreq(adateUpdateRequest.repeatFreq());
		}

		adate.updateIfAllDay(adateUpdateRequest.ifAllDay());
		adate.updateStartsWhen(adateUpdateRequest.startsWhen());
		adate.updateEndsWhen(adateUpdateRequest.endsWhen());
		adate.updateReminders(adateUpdateRequest.reminders());
	}
}
