package com.fixadate.domain.dates.dto.response;

import java.time.LocalDateTime;

public record DatesResponse(
	String title,
	String notes,
	String location,
	LocalDateTime alertWhen,
	LocalDateTime repeatFreq,
	String color,
	boolean ifAllDay,
	LocalDateTime startsWhen,
	LocalDateTime endsWhen,
	boolean reminders
) {
}
