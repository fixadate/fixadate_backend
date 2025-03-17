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
import com.fixadate.domain.main.dto.DatesMemberInfo;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.tag.dto.response.TagResponse;
import com.fixadate.domain.tag.entity.Tag;
import com.fixadate.global.util.RandomValueUtil;
import com.google.api.services.calendar.model.Event;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DatesMapper {

	static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

	private DatesMapper() {
	}

	public static DatesRegisterDto toDatesRegisterDto(final DatesRegisterRequest request) {
		return new DatesRegisterDto(
			request.teamId(),
			request.title(),
			request.notes(),
			LocalDateTime.parse(request.startsWhen(), formatter),
			LocalDateTime.parse(request.endsWhen(), formatter)
		);
	}

	public static DatesUpdateDto toDatesUpdateDto(final DatesUpdateRequest request) {
		return new DatesUpdateDto(
			request.datesId(),
			request.title(),
			request.notes(),
			LocalDateTime.parse(request.startsWhen(), formatter),
			LocalDateTime.parse(request.endsWhen(), formatter)
		);
	}

	public static Dates toEntity(final DatesRegisterDto datesRegisterDto, final Member member) {
		return Dates.builder()
					.title(datesRegisterDto.title())
					.notes(datesRegisterDto.notes())
					.endsWhen(datesRegisterDto.endsWhen())
					.member(member)
					.calendarId(RandomValueUtil.createRandomString(10))
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
					.startsWhen(checkEventDateTimeIsNull(event.getStart()))
					.endsWhen(checkEventDateTimeIsNull(event.getEnd()))
					.member(member)
					.calendarId(RandomValueUtil.createRandomString(10))
					.build();
	}

	public static DatesDto toDatesDto(final Dates dates) {
		return new DatesDto(
			dates.getCalendarId(),
			dates.getTitle(),
			dates.getNotes(),
			dates.getStartsWhen(),
			dates.getEndsWhen()
		);
	}

	public static DatesResponse toDatesResponse(final DatesDto datesDto, List<DatesMemberInfo> datesMemberList) {
		return new DatesResponse(
			datesDto.title(),
			datesDto.notes(),
			datesDto.startsWhen().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
			datesDto.startsWhen().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")),
			datesDto.endsWhen().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")),
			datesDto.calendarId(),
			datesMemberList
		);
	}
	public static DatesViewResponse toDatesViewResponse(final DatesDto datesDto) {
		return new DatesViewResponse(
			datesDto.title(),
			datesDto.notes(),
			datesDto.startsWhen().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")),
			datesDto.endsWhen().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")),
			datesDto.calendarId());
	}

}
