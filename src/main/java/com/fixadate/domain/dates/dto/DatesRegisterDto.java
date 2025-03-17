package com.fixadate.domain.dates.dto;

import java.time.LocalDateTime;

public record DatesRegisterDto(
	Long teamId,
	String title,
	String notes,
	LocalDateTime startsWhen,
	LocalDateTime endsWhen
) {
}