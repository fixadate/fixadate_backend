package com.fixadate.domain.adate.mapper;

import static com.fixadate.domain.tag.mapper.TagMapper.toResponse;
import static com.fixadate.global.util.TimeUtil.getLocalDateTimeFromDate;
import static com.fixadate.global.util.TimeUtil.getLocalDateTimeFromDateTime;

import java.time.LocalDateTime;

import com.fixadate.domain.adate.dto.request.AdateRegisterRequest;
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

	public static Adate registerDtoToEntity(final AdateRegisterRequest adateRegisterRequest, final Member member) {
		return Adate.builder()
					.title(adateRegisterRequest.title())
					.notes(adateRegisterRequest.notes())
					.location(adateRegisterRequest.location())
					.alertWhen(adateRegisterRequest.alertWhen())
					.repeatFreq(adateRegisterRequest.repeatFreq())
					.ifAllDay(adateRegisterRequest.ifAllDay())
					.startsWhen(adateRegisterRequest.startsWhen())
					.endsWhen(adateRegisterRequest.endsWhen())
					.calendarId(RandomValueUtil.createRandomString(10))
					.reminders(adateRegisterRequest.reminders())
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
	 * TODO: [질문] private 메서드 순서를 저는 처음 사용한 메서드 바로 아래 둡니다. 혹시 어떻게 정렬하시나요?
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

	public static AdateResponse toAdateResponse(Adate adate) {
		return new AdateResponse(
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
			adate.isReminders()
		);
	}

	public static AdateViewResponse toAdateViewResponse(final Adate adate) {
		return new AdateViewResponse(
			adate.getTitle(),
			adate.getNotes(),
			adate.getIfAllDay(),
			adate.getStartsWhen(),
			adate.getEndsWhen(),
			adate.getCalendarId(),
			getTagResponse(adate.getTag())
		);
	}

	private static TagResponse getTagResponse(final Tag tag) {
		if (tag == null) {
			return null;
		}

		return toResponse(tag);
	}
}
