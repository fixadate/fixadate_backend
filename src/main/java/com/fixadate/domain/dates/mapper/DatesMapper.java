package com.fixadate.domain.dates.mapper;

import static com.fixadate.global.util.EventTimeUtil.checkEventDateTimeIsNull;
import static com.fixadate.global.util.EventTimeUtil.checkEventIsAllDayType;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.dates.dto.DatesCoordinationDto;
import com.fixadate.domain.dates.dto.DatesCoordinationRegisterDto;
import com.fixadate.domain.dates.dto.DatesDto;
import com.fixadate.domain.dates.dto.DatesRegisterDto;
import com.fixadate.domain.dates.dto.TeamInfo;
import com.fixadate.domain.dates.dto.request.DatesCoordinationRegisterRequest;
import com.fixadate.domain.dates.dto.response.DatesCoordinationResponse;
import com.fixadate.domain.dates.dto.response.DatesMonthlyViewResponse.Team;
import com.fixadate.domain.dates.dto.response.DatesResponse;
import com.fixadate.domain.dates.dto.DatesUpdateDto;
import com.fixadate.domain.dates.dto.request.DatesUpdateRequest;
import com.fixadate.domain.dates.dto.response.DatesViewResponse;
import com.fixadate.domain.dates.entity.Dates;
import com.fixadate.domain.dates.entity.DatesCoordinations;
import com.fixadate.domain.dates.entity.Teams;
import com.fixadate.domain.main.dto.DatesMemberInfo;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.util.RandomValueUtil;
import com.fixadate.global.util.TimeUtil;
import com.google.api.services.calendar.model.Event;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DatesMapper {

	static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
	static DateTimeFormatter minutesFormatter = DateTimeFormatter.ofPattern("HH:mm");

	private DatesMapper() {
	}

//	public static DatesRegisterDto toDatesRegisterDto(final DatesCoordinationRegisterRequest request) {
//		return new DatesRegisterDto(
//			request.teamId(),
//			request.title(),
//			request.notes(),
//			LocalDateTime.parse(request.startsWhen(), formatter),
//			LocalDateTime.parse(request.endsWhen(), formatter)
//		);
//	}

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
					.startsWhen(datesRegisterDto.startsWhen())
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

	public static DatesCoordinationRegisterDto toDatesCoordinationRegisterDto(
		DatesCoordinationRegisterRequest request) {
		// 00:30 -> 30분
		LocalTime localTime = LocalTime.parse(request.time(), minutesFormatter);
		return new DatesCoordinationRegisterDto(
			request.teamId(),
			request.title(),
			LocalDateTime.parse(request.startsWhen(), formatter),
			LocalDateTime.parse(request.endsWhen(), formatter),
	localTime.getHour() * 60 + localTime.getMinute(),
			request.memberIdList()
		);
	}

	public static DatesCoordinations toDatesCoordinationEntity(final DatesCoordinationRegisterDto datesCoordinationRegisterDto, final Member member, final
		Teams team) {
		return DatesCoordinations.builder()
			.title(datesCoordinationRegisterDto.title())
			.ownerId(member.getId())
			.team(team)
			.startsWhen(datesCoordinationRegisterDto.startsWhen())
			.endsWhen(datesCoordinationRegisterDto.endsWhen())
			.minutes(datesCoordinationRegisterDto.minutes())
			.build();
	}

	public static DatesCoordinationDto toDatesCoordinationDto(final DatesCoordinations datesCoordinations) {
		Teams team = datesCoordinations.getTeam();
		return new DatesCoordinationDto(
			datesCoordinations.getId(),
			new TeamInfo(team.getId(), team.getName(), team.getProfileImage(), team.getDescription()),
			datesCoordinations.getTitle(),
			datesCoordinations.getCollectStatus(),
			datesCoordinations.getMinutes(),
			datesCoordinations.getStartsWhen(),
			datesCoordinations.getEndsWhen()
		);
	}

	public static DatesCoordinationResponse toDatesCoordinationResponse(DatesCoordinationDto datesCoordinationDto) {
		return new DatesCoordinationResponse(
			datesCoordinationDto.id(),
			datesCoordinationDto.team(),
			datesCoordinationDto.title(),
			datesCoordinationDto.collectStatus(),
			TimeUtil.convertMinutesToTime(datesCoordinationDto.minutes()),
			datesCoordinationDto.startsWhen().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")),
			datesCoordinationDto.endsWhen().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"))
		);
	}
}
