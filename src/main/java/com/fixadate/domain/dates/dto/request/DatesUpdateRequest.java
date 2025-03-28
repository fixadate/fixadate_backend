package com.fixadate.domain.dates.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public record DatesUpdateRequest(
	@NotBlank(message = "Dates datesId cannot be blank")
	Long datesId,
	@NotBlank(message = "Dates title cannot be blank")
	String title,
	String notes,
	@NotNull(message = "Dates startsWhen cannot be null")
	@Schema(description = "시작일", example = "yyyyMMddHHmmss")
	String startsWhen,
	@NotNull(message = "Dates endsWhen cannot be null")
	@Schema(description = "종료", example = "yyyyMMddHHmmss")
	String endsWhen,
	List<String> memberIdList
) {
}
