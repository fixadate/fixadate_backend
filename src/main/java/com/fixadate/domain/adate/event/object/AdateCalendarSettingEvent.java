package com.fixadate.domain.adate.event.object;

import com.fixadate.domain.member.entity.Member;
import com.google.api.services.calendar.model.Event;

public record AdateCalendarSettingEvent(Event event, Member member) {
}
