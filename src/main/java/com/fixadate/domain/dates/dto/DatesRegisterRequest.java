package com.fixadate.domain.dates.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record DatesRegisterRequest(
	@NotBlank(message = "Dates title cannot be blank")
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
	boolean reminders
) {
}
