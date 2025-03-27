package com.fixadate.domain.notification.event.object;

import com.fixadate.domain.dates.dto.DatesCoordinationDto;
import com.fixadate.domain.dates.entity.DatesCoordinations;
import com.fixadate.domain.dates.entity.Teams;
import com.fixadate.domain.member.entity.Member;
import java.util.List;

public record DatesCoordinationCreateEvent(List<Member> teamMemberList, DatesCoordinationDto datesCoordinationDto) {
}
