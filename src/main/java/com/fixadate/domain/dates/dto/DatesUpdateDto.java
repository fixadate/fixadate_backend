package com.fixadate.domain.dates.dto;

import java.time.LocalDateTime;

public record DatesUpdateDto(
	Long datesId,
	String title,
	String notes,
	LocalDateTime startsWhen,
	LocalDateTime endsWhen
) {
}