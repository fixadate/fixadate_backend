package com.fixadate.domain.adate.dto;

import java.time.LocalDateTime;

public record AdateRegisterDto(
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
