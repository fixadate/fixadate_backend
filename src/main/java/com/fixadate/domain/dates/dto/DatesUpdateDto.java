package com.fixadate.domain.dates.dto;

import java.time.LocalDateTime;

public record DatesUpdateDto(
	String title,
	String notes,
	String location,
	LocalDateTime alertWhen,
	LocalDateTime repeatFreq,
	String tagName,
	boolean ifAllDay,
	LocalDateTime startsWhen,
	LocalDateTime endsWhen,
	boolean reminders
) {
}