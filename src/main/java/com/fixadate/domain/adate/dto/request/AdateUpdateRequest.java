package com.fixadate.domain.adate.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

public record AdateUpdateRequest(
	String title,
	String notes,
	String location,
	LocalDateTime alertWhen,
	LocalDateTime repeatFreq,
	String tagName,
	@NotNull
	boolean ifAllDay,
	@NotNull(message = "Adate startsWhen cannot be null")
	LocalDateTime startsWhen,
	@NotNull(message = "Adate endsWhen cannot be null")
	LocalDateTime endsWhen,
	@NotNull
	boolean reminders
) {
}
