package com.fixadate.domain.notification.event.object;

import com.fixadate.domain.dates.entity.Teams;
import com.fixadate.domain.member.entity.Member;
import java.util.List;

public record AlarmCheckEvent(Member member) {
}
