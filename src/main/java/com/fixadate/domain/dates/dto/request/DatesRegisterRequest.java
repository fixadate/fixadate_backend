package com.fixadate.domain.dates.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record DatesRegisterRequest(
	@NotBlank(message = "Dates team cannot be blank")
	Long teamId,
	@NotBlank(message = "Dates title cannot be blank")
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
	@NotNull(message = "Dates startsWhen cannot be null")
	@Schema(description = "시작일", example = "yyyyMMddHHmmss")
	String startsWhen,
	@NotNull(message = "Dates endsWhen cannot be null")
	@Schema(description = "종료", example = "yyyyMMddHHmmss")
	String endsWhen,
	boolean reminders
) {
}
