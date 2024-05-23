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
	String adateName,
	@NotNull boolean ifAllDay,
	LocalDateTime startsWhen,
	LocalDateTime endsWhen,
	@NotNull boolean reminders) {
}
