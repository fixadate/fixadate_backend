package com.fixadate.domain.dates.dto;

import com.fixadate.domain.dates.entity.DatesCoordinations.CollectStatus;
import java.time.LocalDateTime;

public record DatesCoordinationDto(
	Long id,
	TeamInfo team,
	String title,
	CollectStatus collectStatus,
	int minutes,
	LocalDateTime startsWhen,
	LocalDateTime endsWhen){
}