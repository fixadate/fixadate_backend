package com.fixadate.domain.notification.event.object;

import com.fixadate.domain.dates.entity.Teams;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.util.constant.ExternalCalendar;
import com.google.api.services.calendar.model.Event;
import java.util.List;

public record DatesCreateEvent(List<Member> teamMemberList, Teams team) {
}
