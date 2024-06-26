package com.fixadate.domain.tag.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TagUpdateRequest(
	@NotBlank String name,
	String newColor,
	String newName) {
}
