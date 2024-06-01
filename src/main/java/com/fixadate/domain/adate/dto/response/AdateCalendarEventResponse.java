package com.fixadate.domain.adate.dto.response;

import java.time.LocalDateTime;

public record AdateCalendarEventResponse(
	String title,
	String notes,
	String location,
	LocalDateTime alertWhen,
	LocalDateTime repeatFreq,
	String color,
	boolean ifAllDay,
	LocalDateTime startsWhen,
	LocalDateTime endsWhen,
	String calendarId,
	boolean reminders
) {
}
