package com.fixadate.domain.adate.mapper;

import static com.fixadate.domain.tag.mapper.TagMapper.toResponse;
import static com.fixadate.global.util.EventTimeUtil.checkEventDateTimeIsNull;
import static com.fixadate.global.util.EventTimeUtil.checkEventIsAllDayType;

import com.fixadate.domain.adate.dto.*;
import com.fixadate.domain.adate.dto.request.AdateRegisterRequest;
import com.fixadate.domain.adate.dto.request.AdateUpdateRequest;
import com.fixadate.domain.adate.dto.request.ToDoStatusUpdateRequest;
import com.fixadate.domain.adate.dto.request.TodoRegisterRequest;
import com.fixadate.domain.adate.dto.response.AdateResponse;
import com.fixadate.domain.adate.dto.response.AdateViewResponse;
import com.fixadate.domain.adate.dto.response.ToDoResponse;
import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.entity.ToDo;
import com.fixadate.domain.adate.entity.ToDoStatus;
import com.fixadate.domain.member.dto.response.MemberInfoResponse;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.tag.dto.response.TagResponse;
import com.fixadate.domain.tag.entity.Tag;
import com.fixadate.global.util.RandomValueUtil;
import com.google.api.services.calendar.model.Event;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.commons.lang3.StringUtils;

public class AdateMapper {

	private AdateMapper() {
	}

	static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

	public static AdateRegisterDto toAdateRegisterDto(final AdateRegisterRequest request) {
		return new AdateRegisterDto(
			request.title(),
			request.notes(),
			request.location(),
			StringUtils.isBlank(request.alertWhen()) ?  null :LocalDateTime.parse(request.alertWhen(), formatter),
			StringUtils.isBlank(request.repeatFreq()) ?  null : LocalDateTime.parse(request.repeatFreq(), formatter),
			request.tagName(),
			request.ifAllDay(),
			LocalDateTime.parse(request.startsWhen(), formatter),
			LocalDateTime.parse(request.endsWhen(), formatter),
			request.reminders()
		);
	}

	public static AdateUpdateDto toAdateUpdateDto(final AdateUpdateRequest request) {
		return new AdateUpdateDto(
			request.title(),
			request.notes(),
			request.location(),
			StringUtils.isBlank(request.alertWhen()) ?  null :LocalDateTime.parse(request.alertWhen(), formatter),
			StringUtils.isBlank(request.repeatFreq()) ?  null : LocalDateTime.parse(request.repeatFreq(), formatter),
			request.tagName(),
			request.ifAllDay(),
			LocalDateTime.parse(request.startsWhen(), formatter),
			LocalDateTime.parse(request.endsWhen(), formatter),
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
			getTagName(adate.tag()),
			getColor(adate.tag()),
			adate.ifAllDay(),
			adate.startsWhen().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
			adate.startsWhen().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")),
			adate.endsWhen().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")),
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

	private static String getTagName(final TagResponse tag) {
		if (tag == null) {
			return null;
		}

		return tag.name();
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

	public static ToDoRegisterDto toToDoRegisterDto(final TodoRegisterRequest todoRegisterRequest, final Member member) {
		return new ToDoRegisterDto(
				todoRegisterRequest.title(),
				todoRegisterRequest.date(),
				member
		);
	}

	public static ToDo toEntity(final ToDoRegisterDto todoRegisterDto) {
		return ToDo.builder()
				.title(todoRegisterDto.title())
				.date(todoRegisterDto.date())
				.toDoStatus(ToDoStatus.SCHEDULED)
				.member(todoRegisterDto.member())
				.build();
	}

	public static ToDoResponse toToDoResponse(ToDo toDo) {
		MemberInfoResponse memberInfoResponse = new MemberInfoResponse(
				toDo.getMember().getName(),
				toDo.getMember().getNickname(),
				toDo.getMember().getBirth(),
				toDo.getMember().getGender(),
				toDo.getMember().getSignatureColor(),
				toDo.getMember().getProfession(),
				toDo.getMember().getProfileImg()
		);
		return new ToDoResponse(
				toDo.getTitle(),
				toDo.getToDoStatus(),
				toDo.getDate(),
				memberInfoResponse
		);
	}
}
