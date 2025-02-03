package com.fixadate.domain.notification.event.object;

import com.fixadate.domain.invitation.entity.Invitation;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.util.constant.ExternalCalendar;
import com.google.api.services.calendar.model.Event;

// 팀 초대의 경우, confirm창을 통해 수락/거절을 할 수 있으므로, Invitation 객체를 사용한다.
public record TeamInvitationEvent(Invitation invitation, Member receiver, String teamName) {
}
