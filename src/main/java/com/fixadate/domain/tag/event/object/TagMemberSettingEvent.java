package com.fixadate.domain.tag.event.object;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.util.constant.ExternalCalendar;

public record TagMemberSettingEvent(Member member, ExternalCalendar externalCalendar) {
}
