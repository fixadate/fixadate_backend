package com.fixadate.domain.adate.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

public record AdateUpdateRequest(
	String title,
	String notes,
	String location,
	@Schema(description = "알람일시", example = "yyyyMMddHHmmss")
	String alertWhen,
	@Schema(description = "반복일시", example = "yyyyMMddHHmmss")
	String repeatFreq,
	@Schema(description = "본인 갖고 있는 태크명들 중 하나여야한다.", example = "태그명")
	String tagName,
	@NotNull
	boolean ifAllDay,
	@NotNull(message = "Adate startsWhen cannot be null")
	@Schema(description = "시작일", example = "yyyyMMddHHmmss")
	String startsWhen,
	@NotNull(message = "Adate endsWhen cannot be null")
	@Schema(description = "종료", example = "yyyyMMddHHmmss")
	String endsWhen,
	@NotNull
	boolean reminders
) {
}
