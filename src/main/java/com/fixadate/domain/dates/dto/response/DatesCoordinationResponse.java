package com.fixadate.domain.dates.dto.response;

import com.fixadate.domain.dates.dto.TeamInfo;
import com.fixadate.domain.dates.entity.DatesCoordinations.CollectStatus;
import java.time.LocalDateTime;

public record DatesCoordinationResponse(
	Long datesCoordinationId,
	TeamInfo team,
	String title,
	CollectStatus collectStatus,
	String time,
	String startsWhen,
	String endsWhen){
}