package com.fixadate.domain.tag.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TagUpdateRequest(

	@NotBlank(message = "Tag name cannot be blank")
	String name,

	String newColor,

	String newName
) {
}
