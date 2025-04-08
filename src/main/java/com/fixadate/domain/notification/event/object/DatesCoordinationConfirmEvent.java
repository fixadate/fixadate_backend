package com.fixadate.domain.notification.event.object;

import com.fixadate.domain.dates.dto.DatesCoordinationDto;
import com.fixadate.domain.dates.dto.DatesDto;
import com.fixadate.domain.member.entity.Member;

public record DatesCoordinationConfirmEvent(Member participant, DatesDto datesDto) {
}
