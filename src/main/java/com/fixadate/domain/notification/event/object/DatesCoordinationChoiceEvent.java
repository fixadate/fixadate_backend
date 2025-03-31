package com.fixadate.domain.notification.event.object;

import com.fixadate.domain.dates.dto.DatesCoordinationDto;
import com.fixadate.domain.dates.entity.TeamMembers;
import com.fixadate.domain.member.entity.Member;
import java.util.List;

public record DatesCoordinationChoiceEvent(Member proponent, DatesCoordinationDto datesCoordinationDto) {
}
