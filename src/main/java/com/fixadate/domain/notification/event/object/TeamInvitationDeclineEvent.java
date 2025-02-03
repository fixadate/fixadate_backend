package com.fixadate.domain.notification.event.object;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.util.constant.ExternalCalendar;
import com.google.api.services.calendar.model.Event;

public record TeamInvitationDeclineEvent(Member receiver, String teamName, String participantName) {
}
