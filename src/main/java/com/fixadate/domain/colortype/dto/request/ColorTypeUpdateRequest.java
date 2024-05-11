package com.fixadate.domain.colortype.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ColorTypeUpdateRequest(
	@NotBlank String color,
	@NotBlank String newColor,
	String newName) {
}
