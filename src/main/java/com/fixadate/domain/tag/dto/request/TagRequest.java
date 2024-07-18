package com.fixadate.domain.tag.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TagRequest(
	@NotBlank(message = "Tag color cannot be blank")
	String color,
	@NotBlank(message = "Tag name cannot be blank")
	String name) {
}
