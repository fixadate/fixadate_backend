package com.fixadate.domain.adate.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdateRegisterRequest(
	@NotBlank(message = "Adate title cannot be blank") String title,
	String notes,
	String location,
	LocalDateTime alertWhen,
	LocalDateTime repeatFreq,
	String tagName,
	boolean ifAllDay,
	@NotNull(message = "Adate startsWhen cannot be null")
	LocalDateTime startsWhen,
	@NotNull(message = "Adate endsWhen cannot be null")
	LocalDateTime endsWhen,
	boolean reminders
) {
}