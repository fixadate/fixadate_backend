package com.fixadate.domain.dates.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record DatesUpdateRequest(
	String title,
	String notes,
	String location,
	@Schema(description = "알람일시", example = "yyyyMMddHHmmss")
	String alertWhen,
	@Schema(description = "반복일시", example = "yyyyMMddHHmmss")
	String repeatFreq,
	String tagName,
	@NotNull
	boolean ifAllDay,
	@NotNull(message = "Adate startsWhen cannot be null")
	@Schema(description = "시작일시", example = "yyyyMMddHHmmss")
	String startsWhen,
	@NotNull(message = "Adate endsWhen cannot be null")
	@Schema(description = "종료일시", example = "yyyyMMddHHmmss")
	String endsWhen,
	@NotNull
	boolean reminders
) {
}
