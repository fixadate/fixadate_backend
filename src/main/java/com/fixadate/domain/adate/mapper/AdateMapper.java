package com.fixadate.domain.adate.mapper;

import static com.fixadate.domain.tag.mapper.TagMapper.toResponse;
import static com.fixadate.global.util.TimeUtil.getLocalDateTimeFromDate;
import static com.fixadate.global.util.TimeUtil.getLocalDateTimeFromDateTime;

import java.time.LocalDateTime;

import com.fixadate.domain.adate.dto.AdateDto;
import com.fixadate.domain.adate.dto.AdateRegisterDto;
import com.fixadate.domain.adate.dto.AdateUpdateDto;
import com.fixadate.domain.adate.dto.request.AdateRegisterRequest;
import com.fixadate.domain.adate.dto.request.AdateUpdateRequest;
import com.fixadate.domain.adate.dto.response.AdateResponse;
import com.fixadate.domain.adate.dto.response.AdateViewResponse;
import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.tag.dto.response.TagResponse;
import com.fixadate.domain.tag.entity.Tag;
import com.fixadate.global.util.RandomValueUtil;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AdateMapper {

	public static AdateRegisterDto toDto(final AdateRegisterRequest request) {
		return new AdateRegisterDto(
			request.title(),
			request.notes(),
			request.location(),
			request.alertWhen(),
			request.repeatFreq(),
			request.tagName(),
			request.ifAllDay(),
			request.startsWhen(),
			request.endsWhen(),
			request.reminders()
		);
	}

	public static AdateUpdateDto toDto(final AdateUpdateRequest request) {
		return new AdateUpdateDto(
			request.title(),
			request.notes(),
			request.location(),
			request.alertWhen(),
			request.repeatFreq(),
			request.tagName(),
			request.ifAllDay(),
			request.startsWhen(),
			request.endsWhen(),
			request.reminders()
		);
	}

	public static Adate registerDtoToEntity(final AdateRegisterDto adateRegisterDto, final Member member) {
		return Adate.builder()
					.title(adateRegisterDto.title())
					.notes(adateRegisterDto.notes())
					.location(adateRegisterDto.location())
					.alertWhen(adateRegisterDto.alertWhen())
					.repeatFreq(adateRegisterDto.repeatFreq())
					.ifAllDay(adateRegisterDto.ifAllDay())
					.startsWhen(adateRegisterDto.startsWhen())
					.endsWhen(adateRegisterDto.endsWhen())
					.calendarId(RandomValueUtil.createRandomString(10))
					.reminders(adateRegisterDto.reminders())
					.member(member)
					.build();
	}

	public static Adate eventToEntity(final Event event) {
		return Adate.builder()
					.title(event.getSummary())
					.notes(event.getDescription())
					.location(event.getLocation())
					.color(event.getColorId())
					.startsWhen(checkEventDateTimeIsNull(event.getStart()))
					.endsWhen(checkEventDateTimeIsNull(event.getEnd()))
					.ifAllDay(checkEventIsAllDayType(event.getStart()))
					.calendarId(event.getId())
					.etag(event.getEtag())
					.reminders(event.getReminders().getUseDefault())
					.build();
	}

	/**
	 * TODO: [질문] 제 경우 private 메서드 순서를 저는 처음 사용한 메서드 바로 아래 둡니다. 혹시 어떻게 정렬하시나요?
	 * TODO: [의견] 아래 check 관련 메서드는 adate의 역할이 아니라고 생각했습니다.
	 *  check 관련 해당 메서드를 사용하는 곳은 adateMapper 뿐이기도 하고요. 그래서 이 클래스로 이동해봤습니다.
	 */
	private static LocalDateTime checkEventDateTimeIsNull(final EventDateTime eventDateTime) {
		if (eventDateTime.getDateTime() == null) {
			return getLocalDateTimeFromDate(eventDateTime.getDate());
		}

		return getLocalDateTimeFromDateTime(eventDateTime.getDateTime());
	}

	private static boolean checkEventIsAllDayType(final EventDateTime eventDateTime) {
		return eventDateTime.getDateTime() == null;
	}

	public static Adate eventToEntity(final Event event, final Member member) {
		return Adate.builder()
					.title(event.getSummary())
					.notes(event.getDescription())
					.location(event.getLocation())
					.color(event.getColorId())
					.startsWhen(checkEventDateTimeIsNull(event.getStart()))
					.endsWhen(checkEventDateTimeIsNull(event.getEnd()))
					.ifAllDay(checkEventIsAllDayType(event.getStart()))
					.calendarId(event.getId())
					.etag(event.getEtag())
					.reminders(event.getReminders().getUseDefault())
					.member(member)
					.build();
	}

	public static AdateDto toAdateDto(final Adate adate) {
		return new AdateDto(
			adate.getId(),
			adate.getTitle(),
			adate.getNotes(),
			adate.getLocation(),
			adate.getAlertWhen(),
			adate.getRepeatFreq(),
			adate.getColor(),
			adate.getIfAllDay(),
			adate.getStartsWhen(),
			adate.getEndsWhen(),
			adate.getCalendarId(),
			adate.getEtag(),
			adate.isReminders(),
			getTagResponse(adate.getTag())
		);
	}

	public static AdateResponse toAdateResponse(final AdateDto adate) {
		return new AdateResponse(
			adate.title(),
			adate.notes(),
			adate.location(),
			adate.alertWhen(),
			adate.repeatFreq(),
			adate.color(),
			adate.ifAllDay(),
			adate.startsWhen(),
			adate.endsWhen(),
			adate.calendarId(),
			adate.reminders()
		);
	}

	public static AdateViewResponse toAdateViewResponse(final AdateDto adate) {
		return new AdateViewResponse(
			adate.title(),
			adate.notes(),
			adate.ifAllDay(),
			adate.startsWhen(),
			adate.endsWhen(),
			adate.calendarId(),
			adate.tag()
		);
	}

	private static TagResponse getTagResponse(final Tag tag) {
		if (tag == null) {
			return null;
		}

		return toResponse(tag);
	}
}
