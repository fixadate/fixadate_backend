package com.fixadate.domain.adate.mapper;

import static com.fixadate.domain.adate.entity.Adate.*;

import com.fixadate.domain.adate.dto.request.AdateRegistRequest;
import com.fixadate.domain.adate.dto.response.AdateResponse;
import com.fixadate.domain.adate.dto.response.AdateViewResponse;
import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.member.entity.Member;
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

	public static Adate registDtoToEntity(AdateRegistRequest adateRegistRequest, Member member) {
		return builder()
			.title(adateRegistRequest.title())
			.notes(adateRegistRequest.notes())
			.location(adateRegistRequest.location())
			.alertWhen(adateRegistRequest.alertWhen())
			.repeatFreq(adateRegistRequest.repeatFreq())
			.ifAllDay(adateRegistRequest.ifAllDay())
			.startsWhen(adateRegistRequest.startsWhen())
			.endsWhen(adateRegistRequest.endsWhen())
			.calendarId(RandomValueUtil.createRandomString(10))
			.reminders(adateRegistRequest.reminders())
			.member(member)
			.build();
	}

	public static Adate eventToEntity(Event event, Member member, Tag tag) {
		return builder()
			.title(event.getSummary())
			.notes(event.getDescription())
			.location(event.getLocation())
			.color(event.getColorId())
			.startsWhen(checkStartDateTimeIsNull(event))
			.endsWhen(checkEndDateTimeIsNull(event))
			.ifAllDay(checkEventIsAllDayType(event))
			.calendarId(event.getId())
			.color(tag.getColor())
			.etag(event.getEtag())
			.reminders(event.getReminders().getUseDefault())
			.tag(tag)
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

	public static AdateViewResponse toAdateViewResponse(Adate adate) {
		return new AdateViewResponse(
			adate.getTitle(),
			adate.getColor(),
			adate.getIfAllDay(),
			adate.getStartsWhen(),
			adate.getEndsWhen(),
			adate.getCalendarId()
		);
	}
}
