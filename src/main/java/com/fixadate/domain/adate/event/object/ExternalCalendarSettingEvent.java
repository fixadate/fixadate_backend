package com.fixadate.domain.adate.event.object;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.util.constant.ExternalCalendar;
import com.google.api.services.calendar.model.Event;

// TODO: [추후] Event는 Google 라이브러리 타입이므로, 다른 캘린더로 확장할 수 있도록 리팩터링 필요
public record ExternalCalendarSettingEvent(Event event, Member member, ExternalCalendar externalCalendar) {
}
