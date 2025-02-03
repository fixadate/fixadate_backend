package com.fixadate.domain.notification.event.object;

import com.fixadate.domain.dates.entity.Teams;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.util.constant.ExternalCalendar;
import com.google.api.services.calendar.model.Event;

public record TeamCalendarCreateEvent(Event event, Member teamMember, Teams team, ExternalCalendar externalCalendar) {
}
