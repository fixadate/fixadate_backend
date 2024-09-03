package com.fixadate.domain.adate.service;

import static com.fixadate.domain.adate.mapper.AdateMapper.toAdateDto;
import static com.fixadate.domain.adate.mapper.AdateMapper.toEntity;
import static com.fixadate.global.exception.ExceptionCode.FORBIDDEN_UPDATE_ADATE;
import static com.fixadate.global.exception.ExceptionCode.INVALID_START_END_TIME;
import static com.fixadate.global.exception.ExceptionCode.NOT_FOUND_ADATE_CALENDAR_ID;
import static com.fixadate.global.util.TimeUtil.getLocalDateTimeFromLocalDate;
import static com.fixadate.global.util.TimeUtil.getLocalDateTimeFromYearAndMonth;
import static com.fixadate.global.util.constant.ConstantValue.ADATE_WITH_COLON;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fixadate.domain.adate.dto.AdateDto;
import com.fixadate.domain.adate.dto.AdateRegisterDto;
import com.fixadate.domain.adate.dto.AdateUpdateDto;
import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.mapper.AdateMapper;
import com.fixadate.domain.adate.service.repository.AdateRepository;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.tag.event.object.TagSettingEvent;
import com.fixadate.global.exception.badrequest.InvalidTimeException;
import com.fixadate.global.exception.forbidden.AdateUpdateForbiddenException;
import com.fixadate.global.exception.notfound.AdateNotFoundException;
import com.fixadate.global.exception.notfound.TagNotFoundException;
import com.fixadate.global.facade.RedisFacade;
import com.fixadate.global.util.constant.ExternalCalendar;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdateService {

	private static final int ADATE_REDIS_DURATION = 20;

	private final AdateRepository adateRepository;
	private final RedisFacade redisFacade;
	private final ApplicationEventPublisher applicationEventPublisher;

	@Transactional(noRollbackFor = TagNotFoundException.class)
	public void registerAdate(final AdateRegisterDto adateRegisterDto, final Member member) {
		final Adate adate = toEntity(adateRegisterDto, member);
		adateRepository.save(adate);

		final String tagName = adateRegisterDto.tagName();
		if (tagName != null && !tagName.isEmpty()) {
			applicationEventPublisher.publishEvent(new TagSettingEvent(adate, tagName));
		}
	}

	@Transactional(noRollbackFor = TagNotFoundException.class)
	public void registerExternalCalendarToAdate(final Adate adate, final ExternalCalendar externalCalendar) {
		final Adate saveAdate = adateRepository.save(adate);

		applicationEventPublisher.publishEvent(new TagSettingEvent(saveAdate, externalCalendar.getTagName()));
	}

	@Transactional
	public AdateDto restoreAdateByCalendarId(final String calendarId) {
		final String key = ADATE_WITH_COLON.getValue() + calendarId;
		final Adate adate = redisFacade.getAndDeleteObjectRedis(key, Adate.class);
		final Adate saveAdate = adateRepository.save(adate);

		return toAdateDto(saveAdate);
	}

	@Transactional(readOnly = true)
	public Optional<Adate> getAdateByCalendarId(final String calendarId) {
		return adateRepository.findAdateByCalendarId(calendarId);
	}

	// TODO: [추후] 회원정보를 받아, 해당 회원의 일정인지 확인 필요
	@Transactional(readOnly = true)
	public AdateDto getAdateInformationByCalendarId(final String calendarId) {
		final Adate adate = getAdateByCalendarId(calendarId).orElseThrow(
			() -> new AdateNotFoundException(NOT_FOUND_ADATE_CALENDAR_ID)
		);

		return toAdateDto(adate);
	}

	@Transactional(readOnly = true)
	public List<AdateDto> getAdateByStartAndEndTime(
		final Member member,
		final LocalDateTime startDateTime,
		final LocalDateTime endDateTime
	) {
		checkStartAndEndTime(startDateTime, endDateTime);
		final List<Adate> adates = adateRepository.findByDateRange(member, startDateTime, endDateTime);

		return adates.stream()
					 .map(AdateMapper::toAdateDto)
					 .toList();
	}

	private void checkStartAndEndTime(final LocalDateTime startTime, final LocalDateTime endTime) {
		if (startTime.isAfter(endTime)) {
			throw new InvalidTimeException(INVALID_START_END_TIME);
		}
	}

	@Transactional(readOnly = true)
	public List<AdateDto> getAdatesByMonth(final Member member, final int year, final int month) {
		final LocalDateTime startTime = getLocalDateTimeFromYearAndMonth(year, month, true);
		final LocalDateTime endTime = getLocalDateTimeFromYearAndMonth(year, month, false);

		return getAdateByStartAndEndTime(member, startTime, endTime);
	}

	@Transactional(readOnly = true)
	public List<AdateDto> getAdatesByDate(
		final Member member,
		final LocalDate firstDay,
		final LocalDate lastDay
	) {
		final LocalDateTime startTime = getLocalDateTimeFromLocalDate(firstDay, true);
		final LocalDateTime endTime = getLocalDateTimeFromLocalDate(lastDay, false);

		return getAdateByStartAndEndTime(member, startTime, endTime);
	}

	@Transactional(noRollbackFor = TagNotFoundException.class)
	public AdateDto updateAdate(
		final Member member,
		final String calendarId,
		final AdateUpdateDto adateUpdateDto
	) {
		final Adate adate = getAdateByCalendarId(calendarId).orElseThrow(
			() -> new AdateNotFoundException(NOT_FOUND_ADATE_CALENDAR_ID)
		);

		validateUserCanUpdate(adate, member);
		updateIfNotNull(adate, adateUpdateDto);

		return toAdateDto(adate);
	}

	private void validateUserCanUpdate(final Adate adate, final Member member) {
		if (!adate.isOwner(member)) {
			throw new AdateUpdateForbiddenException(FORBIDDEN_UPDATE_ADATE);
		}
	}

	private void updateIfNotNull(final Adate adate, final AdateUpdateDto adateUpdateDto) {
		if (adateUpdateDto.tagName() != null) {
			applicationEventPublisher.publishEvent(new TagSettingEvent(adate, adateUpdateDto.tagName()));
		}
		if (adateUpdateDto.title() != null) {
			adate.updateTitle(adateUpdateDto.title());
		}
		if (adateUpdateDto.notes() != null) {
			adate.updateNotes(adateUpdateDto.notes());
		}
		if (adateUpdateDto.location() != null) {
			adate.updateLocation(adateUpdateDto.location());
		}
		if (adateUpdateDto.alertWhen() != null) {
			adate.updateAlertWhen(adateUpdateDto.alertWhen());
		}
		if (adateUpdateDto.repeatFreq() != null) {
			adate.updateRepeatFreq(adateUpdateDto.repeatFreq());
		}

		adate.updateIfAllDay(adateUpdateDto.ifAllDay());
		adate.updateStartsWhen(adateUpdateDto.startsWhen());
		adate.updateEndsWhen(adateUpdateDto.endsWhen());
		adate.updateReminders(adateUpdateDto.reminders());
	}

	@Transactional
	public void removeAdate(final Adate adate) {
		adateRepository.delete(adate);
	}

	@Transactional
	public void removeAdateByCalendarId(final String calendarId) {
		final Adate adate = getAdateByCalendarId(calendarId).orElseThrow(
			() -> new AdateNotFoundException(NOT_FOUND_ADATE_CALENDAR_ID)
		);
		adateRepository.delete(adate);

		final Duration adateDuration = Duration.ofDays(ADATE_REDIS_DURATION);
		redisFacade.removeAndRegisterObject(ADATE_WITH_COLON.getValue() + calendarId, adate, adateDuration);
	}
}
