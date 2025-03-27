package com.fixadate.domain.dates.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record DatesCoordinationRegisterRequest(
	@NotNull(message = "team cannot be blank")
	Long teamId,
	@NotBlank(message = "title cannot be blank")
	@Schema(description = "미팅 이름", example = "")
	String title,
	@NotNull(message = "time cannot be null")
	@Schema(description = "소요시간", example = "00:30, 01:00")
	String time,
	@NotNull(message = "startsWhen cannot be null")
	@Schema(description = "투표 시작일", example = "yyyyMMddHHmmss")
	String startsWhen,
	@NotNull(message = "endsWhen cannot be null")
	@Schema(description = "투표 종료", example = "yyyyMMddHHmmss")
	String endsWhen,
	List<String> memberIdList
) {
}
