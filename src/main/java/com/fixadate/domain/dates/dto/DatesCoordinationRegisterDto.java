package com.fixadate.domain.dates.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public record DatesCoordinationRegisterDto(
	Long teamId,
	String title,
	LocalDateTime startsWhen,
	LocalDateTime endsWhen,
	int minutes,
	List<String> memberIdList
) {
}