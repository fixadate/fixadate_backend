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

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fixadate.domain.adate.dto.request.AdateRegistRequest;
import com.fixadate.domain.adate.dto.request.AdateUpdateRequest;
import com.fixadate.domain.adate.dto.response.AdateResponse;
import com.fixadate.domain.adate.dto.response.AdateViewResponse;
import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.mapper.AdateMapper;
import com.fixadate.domain.adate.repository.AdateQueryRepository;
import com.fixadate.domain.adate.repository.AdateRepository;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.tag.entity.Tag;
import com.fixadate.domain.tag.repository.TagRepository;
import com.fixadate.global.exception.badRequest.InvalidTimeException;
import com.fixadate.global.exception.badRequest.RedisRequestException;
import com.fixadate.global.exception.notFound.AdateNotFoundException;
import com.fixadate.global.exception.notFound.TagNotFoundException;
import com.google.api.services.calendar.model.Event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdateService {
	private final AdateRepository adateRepository;
	private final AdateQueryRepository adateQueryRepository;
	private final TagRepository tagRepository;
	private final RedisTemplate<String, Object> redisJsonTemplate;
	private final ObjectMapper objectMapper;
	private final ObjectProvider<AdateService> adateServiceObjectProvider;

	@Transactional
	public void registAdateEvent(AdateRegistRequest adateRegistRequest, String tagName, Member member) {
		Adate adate = registDtoToEntity(adateRegistRequest, member);
		if (tagName != null && !tagName.isEmpty()) {
			setAdateTag(adate, member, tagName);
		}
		adateRepository.save(adate);
	}

	@Transactional
	public void setAdateTag(Adate adate, Member member, String tagName) {
		Tag tag = tagRepository.findTagByNameAndMember(tagName, member)
			.orElseThrow(() -> new TagNotFoundException(NOT_FOUND_TAG_MEMBER_NAME));
		adate.setTag(tag);
	}

	public AdateResponse restoreAdateByCalendarId(String calendarId) {
		try {
			Adate adate = objectMapper.convertValue(
				redisJsonTemplate.opsForValue().getAndDelete(ADATE + calendarId),
				Adate.class
			);
			return toAdateResponse(adateRepository.save(adate));
		} catch (Exception e) {
			RedisRequestException.handleRedisException(e);
			return null;
		}
	}

	public void registEvent(Event event, Member member) {
		Tag tag = getGoogleCalendarTagFromMember(member);
		Adate adate = eventToEntity(event, member, tag);
		adateRepository.save(adate);
	}

	public Tag getGoogleCalendarTagFromMember(Member member) {
		return tagRepository.findTagByNameAndMember(GOOGLE_CALENDAR.getValue(), member)
			.orElseThrow(() -> new TagNotFoundException(NOT_FOUND_TAG_MEMBER_NAME));
	}

	@Transactional
	public void removeEvents(List<Adate> adates) {
		adateRepository.deleteAll(adates);
	}

	@Transactional
	public void removeAdateByCalendarId(String calendarId) {
		Adate adate = getAdateByCalendarId(calendarId).orElseThrow(
			() -> new AdateNotFoundException(NOT_FOUND_ADATE_CALENDAR_ID));

		adateRepository.delete(adate);

		AdateService adateService = adateServiceObjectProvider.getObject();
		try {
			adateService.setAdateOnRedis(adate);
		} catch (Exception e) {
			RedisRequestException.handleRedisException(e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void setAdateOnRedis(Adate adate) {
		Duration duration = Duration.ofDays(20);
		redisJsonTemplate.opsForValue().set(ADATE + adate.getCalendarId(), adate, duration);
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

	@Transactional
	public AdateResponse updateAdate(String calendarId, AdateUpdateRequest adateUpdateRequest, Member member) {
		Adate adate = getAdateByCalendarId(calendarId).orElseThrow(
			() -> new AdateNotFoundException(NOT_FOUND_ADATE_CALENDAR_ID));

		adate.updateAdate(adateUpdateRequest);
		setAdateTag(adate, member, adateUpdateRequest.tagName());
		return toAdateResponse(adate);
	}

	private List<AdateViewResponse> getResponseDtosFromAdateList(List<Adate> adates) {
		return adates.stream().map(AdateMapper::toAdateViewResponse).toList();
	}

	private AdateResponse getAdateResponse(Adate adate) {
		return toAdateResponse(adate);
	}
}
