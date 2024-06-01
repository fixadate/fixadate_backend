package com.fixadate.domain.tag.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TagRequest(
	@NotBlank String color,
	@NotBlank String name) {
}
