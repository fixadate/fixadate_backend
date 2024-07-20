package com.fixadate.domain.adate.mapper;

import static com.fixadate.domain.adate.entity.Adate.*;
import static com.fixadate.domain.tag.mapper.TagMapper.*;

import com.fixadate.domain.adate.dto.request.AdateRegisterRequest;
import com.fixadate.domain.adate.dto.response.AdateResponse;
import com.fixadate.domain.adate.dto.response.AdateViewResponse;
import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.tag.dto.response.TagResponse;
import com.fixadate.domain.tag.entity.Tag;
import com.fixadate.global.util.RandomValueUtil;
import com.google.api.services.calendar.model.Event;

/**
 *
 * @author yongjunhong
 * @since 2024. 6. 1.
 */
public class AdateMapper {

	private AdateMapper() {
	}

	public static Adate registerDtoToEntity(AdateRegisterRequest adateRegisterRequest, Member member) {
		return builder()
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

	public static Adate eventToEntity(Event event) {
		return builder()
			.title(event.getSummary())
			.notes(event.getDescription())
			.location(event.getLocation())
			.color(event.getColorId())
			.startsWhen(checkStartDateTimeIsNull(event))
			.endsWhen(checkEndDateTimeIsNull(event))
			.ifAllDay(checkEventIsAllDayType(event))
			.calendarId(event.getId())
			.etag(event.getEtag())
			.reminders(event.getReminders().getUseDefault())
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

	public static AdateViewResponse toAdateViewResponse(Adate adate) {
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

	private static TagResponse getTagResponse(Tag tag) {
		if (tag == null) {
			return null;
		}
		return toResponse(tag);
	}
}
