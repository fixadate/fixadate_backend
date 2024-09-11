package com.fixadate.domain.adate.mapper;

import static com.fixadate.domain.tag.mapper.TagMapper.toResponse;
import static com.fixadate.global.util.EventTimeUtil.checkEventDateTimeIsNull;
import static com.fixadate.global.util.EventTimeUtil.checkEventIsAllDayType;

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

public class AdateMapper {

	private AdateMapper() {
	}

	public static AdateRegisterDto toAdateRegisterDto(final AdateRegisterRequest request) {
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

	public static AdateUpdateDto toAdateUpdateDto(final AdateUpdateRequest request) {
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

	public static Adate toEntity(final AdateRegisterDto adateRegisterDto, final Member member) {
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

	public static Adate toEntity(final Event event) {
		return Adate.builder()
					.title(event.getSummary())
					.notes(event.getDescription())
					.location(event.getLocation())
					.startsWhen(checkEventDateTimeIsNull(event.getStart()))
					.endsWhen(checkEventDateTimeIsNull(event.getEnd()))
					.ifAllDay(checkEventIsAllDayType(event.getStart()))
					.calendarId(event.getId())
					.etag(event.getEtag())
					.reminders(event.getReminders().getUseDefault())
					.build();
	}

	public static Adate toEntity(final Event event, final Member member) {
		return Adate.builder()
					.title(event.getSummary())
					.notes(event.getDescription())
					.location(event.getLocation())
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
			adate.isIfAllDay(),
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
			getColor(adate.tag()),
			adate.ifAllDay(),
			adate.startsWhen(),
			adate.endsWhen(),
			adate.calendarId(),
			adate.reminders()
		);
	}

	private static String getColor(final TagResponse tag) {
		if (tag == null) {
			return null;
		}

		return tag.color();
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
