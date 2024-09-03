package com.fixadate.domain.adate.service;

import static com.fixadate.domain.adate.mapper.AdateMapper.registerDtoToEntity;
import static com.fixadate.domain.adate.mapper.AdateMapper.toAdateDto;
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
		final Adate adate = registerDtoToEntity(adateRegisterDto, member);
		adateRepository.save(adate);

		final String tagName = adateRegisterDto.tagName();
		if (tagName != null && !tagName.isEmpty()) {
			applicationEventPublisher.publishEvent(new TagSettingEvent(adate, tagName));
		}
	}

	@Transactional
	public AdateDto restoreAdateByCalendarId(final String calendarId) {
		final String key = ADATE_WITH_COLON.getValue() + calendarId;
		final Adate adate = redisFacade.getAndDeleteObjectRedis(key, Adate.class);
		final Adate saveAdate = adateRepository.save(adate);

		return toAdateDto(saveAdate);
	}

	@Transactional(noRollbackFor = TagNotFoundException.class)
	public void registerExternalCalendarToAdate(final Adate adate, final ExternalCalendar externalCalendar) {
		final Adate saveAdate = adateRepository.save(adate);

		applicationEventPublisher.publishEvent(new TagSettingEvent(saveAdate, externalCalendar.getTagName()));
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

	// TODO: [질문] 해당 메서드에서는 checkStartAndEndTime을 확인하지 않는 이유가 있을까요?
	@Transactional(readOnly = true)
	public List<AdateDto> getAdateByStartAndEndTime(
		final Member member,
		final LocalDateTime startDateTime,
		final LocalDateTime endDateTime
	) {
		final List<Adate> adates = adateRepository.findByDateRange(member, startDateTime, endDateTime);

		return adates.stream()
					 .map(AdateMapper::toAdateDto)
					 .toList();
	}

	// TODO: [질문] 여기서 체크가 필요할지 의문입니다. getLocalDate~를 통해 유효한 시작일과 종료일이 설정된 것이 아닐까요?
	//  여기서 검증한다면, TimeUtil의 유효성을 검증하는 느낌인데, 오히려 클라이언트에서 전달 받은 값을 검증하는 게 적절하지 않을까 생각합니다.
	@Transactional(readOnly = true)
	public List<AdateDto> getAdatesByMonth(final Member member, final int year, final int month) {
		final LocalDateTime startTime = getLocalDateTimeFromYearAndMonth(year, month, true);
		final LocalDateTime endTime = getLocalDateTimeFromYearAndMonth(year, month, false);
		checkStartAndEndTime(startTime, endTime);

		return getAdateByStartAndEndTime(member, startTime, endTime);
	}

	private void checkStartAndEndTime(final LocalDateTime startTime, final LocalDateTime endTime) {
		if (startTime.isAfter(endTime)) {
			throw new InvalidTimeException(INVALID_START_END_TIME);
		}
	}

	// TODO: [질문] 여기서 week는 한주차라는 의미가 아닌걸까요? 시간만 없을 뿐 getAdateByStartAndEndTime와 다른점을 잘 모르겠습니다.
	@Transactional(readOnly = true)
	public List<AdateDto> getAdatesByWeek(
		final Member member,
		final LocalDate firstDay,
		final LocalDate lastDay
	) {
		final LocalDateTime startTime = getLocalDateTimeFromLocalDate(firstDay, true);
		final LocalDateTime endTime = getLocalDateTimeFromLocalDate(lastDay, false);
		checkStartAndEndTime(startTime, endTime);

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
}
