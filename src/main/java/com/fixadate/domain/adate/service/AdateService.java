package com.fixadate.domain.adate.service;

import static com.fixadate.domain.adate.mapper.AdateMapper.*;
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
import com.fixadate.domain.adate.dto.response.AdateResponse;
import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.mapper.AdateMapper;
import com.fixadate.domain.adate.repository.AdateQueryRepository;
import com.fixadate.domain.adate.repository.AdateRepository;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.tag.entity.Tag;
import com.fixadate.domain.tag.repository.TagRepository;
import com.fixadate.global.exception.badRequest.InvalidTimeException;
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

	@Transactional
	public void registEvents(List<Event> events, Member member) {
		Tag tag = getGoogleCalendarTagFromMember(member);
		List<Adate> adates = events.stream()
			.map(event -> eventToEntity(event, member, tag))
			.toList();
		adateRepository.saveAll(adates);
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
	public List<AdateResponse> getAdateByStartAndEndTime(Member member, LocalDateTime startDateTime,
		LocalDateTime endDateTime) {
		List<Adate> adates = adateQueryRepository.findByDateRange(member, startDateTime, endDateTime);
		return getResponseDto(adates);
	}

	@Transactional(readOnly = true)
	public List<AdateResponse> getAdatesByMonth(int year, int month, Member member) {
		LocalDateTime startTime = getLocalDateTimeFromYearAndMonth(year, month, true);
		LocalDateTime endTime = getLocalDateTimeFromYearAndMonth(year, month, false);
		checkStartAndEndTime(startTime, endTime);

		return getAdateByStartAndEndTime(member, startTime, endTime);
	}

	@Transactional(readOnly = true)
	public List<AdateResponse> getAdatesByWeek(LocalDate firstDay, LocalDate lastDay, Member member) {
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
	public AdateResponse updateAdate(String calendarId, AdateUpdateRequest adateUpdateRequest,
		Member member) {
		Adate adate = getAdateByCalendarId(calendarId).orElseThrow(
			() -> new AdateNotFoundException(NOT_FOUND_ADATE_CALENDAR_ID));

		adate.updateAdate(adateUpdateRequest);
		setAdateTag(adate, member, adateUpdateRequest.tagName());
		return toResponse(adate);
	}

	private List<AdateResponse> getResponseDto(List<Adate> adates) {
		return adates.stream()
			.map(AdateMapper::toResponse)
			.toList();
	}
}
