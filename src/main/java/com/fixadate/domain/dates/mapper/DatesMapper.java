package com.fixadate.domain.dates.mapper;

import static com.fixadate.domain.tag.mapper.TagMapper.toResponse;
import static com.fixadate.global.util.EventTimeUtil.checkEventDateTimeIsNull;
import static com.fixadate.global.util.EventTimeUtil.checkEventIsAllDayType;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.dates.dto.DatesDto;
import com.fixadate.domain.dates.dto.DatesRegisterDto;
import com.fixadate.domain.dates.dto.request.DatesRegisterRequest;
import com.fixadate.domain.dates.dto.response.DatesResponse;
import com.fixadate.domain.dates.dto.DatesUpdateDto;
import com.fixadate.domain.dates.dto.request.DatesUpdateRequest;
import com.fixadate.domain.dates.dto.response.DatesViewResponse;
import com.fixadate.domain.dates.entity.Dates;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.tag.dto.response.TagResponse;
import com.fixadate.domain.tag.entity.Tag;
import com.google.api.services.calendar.model.Event;

public class DatesMapper {

	private DatesMapper() {
	}

	public static DatesRegisterDto toDatesRegisterDto(final DatesRegisterRequest request) {
		return new DatesRegisterDto(
			request.teamId(),
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

	public static DatesUpdateDto toDatesUpdateDto(final DatesUpdateRequest request) {
		return new DatesUpdateDto(
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

	public static Dates toEntity(final DatesRegisterDto datesRegisterDto, final Member member) {
		return Dates.builder()
					.title(datesRegisterDto.title())
					.notes(datesRegisterDto.notes())
					.location(datesRegisterDto.location())
					.alertWhen(datesRegisterDto.alertWhen())
					.repeatFreq(datesRegisterDto.repeatFreq())
					.ifAllDay(datesRegisterDto.ifAllDay())
					.startsWhen(datesRegisterDto.startsWhen())
					.endsWhen(datesRegisterDto.endsWhen())
					.reminders(datesRegisterDto.reminders())
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

	public static Dates toEntity(final Event event, final Member member) {
		return Dates.builder()
					.title(event.getSummary())
					.notes(event.getDescription())
					.location(event.getLocation())
					.startsWhen(checkEventDateTimeIsNull(event.getStart()))
					.endsWhen(checkEventDateTimeIsNull(event.getEnd()))
					.ifAllDay(checkEventIsAllDayType(event.getStart()))
					.etag(event.getEtag())
					.reminders(event.getReminders().getUseDefault())
					.member(member)
					.build();
	}

	public static DatesDto toDatesDto(final Dates dates) {
		return new DatesDto(
			dates.getId(),
			dates.getTitle(),
			dates.getNotes(),
			dates.getLocation(),
			dates.getAlertWhen(),
			dates.getRepeatFreq(),
			dates.isIfAllDay(),
			dates.getStartsWhen(),
			dates.getEndsWhen(),
			dates.getEtag(),
			dates.isReminders(),
			getTagResponse(dates.getTag())
		);
	}

	public static DatesResponse toDatesResponse(final DatesDto datesDto) {
		return new DatesResponse(
			datesDto.title(),
			datesDto.notes(),
			datesDto.location(),
			datesDto.alertWhen(),
			datesDto.repeatFreq(),
			getColor(datesDto.tag()),
			datesDto.ifAllDay(),
			datesDto.startsWhen(),
			datesDto.endsWhen(),
			datesDto.reminders()
		);
	}

	private static String getColor(final TagResponse tag) {
		if (tag == null) {
			return null;
		}

		return tag.color();
	}

	public static DatesViewResponse toDatesViewResponse(final DatesDto datesDto) {
		return new DatesViewResponse(
			datesDto.title(),
			datesDto.notes(),
			datesDto.ifAllDay(),
			datesDto.startsWhen(),
			datesDto.endsWhen(),
			datesDto.tag()
		);
	}

	private static TagResponse getTagResponse(final Tag tag) {
		if (tag == null) {
			return null;
		}

		return toResponse(tag);
	}
}
