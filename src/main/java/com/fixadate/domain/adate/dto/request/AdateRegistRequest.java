package com.fixadate.domain.adate.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;

public record AdateRegistRequest(
	@NotBlank(message = "Adate title cannot be blank") String title,
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