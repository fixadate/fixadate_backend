package com.fixadate.domain.colortype.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ColorTypeUpdateRequest(
	@NotBlank String name,
	String newColor,
	String newName) {
}
