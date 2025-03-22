package com.fixadate.domain.notification.event.object;

import com.fixadate.domain.member.entity.Member;

public record TeamMemberDeleteEvent(Member receiver, String teamName) {
}
