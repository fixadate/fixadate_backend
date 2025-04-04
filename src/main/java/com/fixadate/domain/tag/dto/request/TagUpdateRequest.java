package com.fixadate.domain.tag.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record TagUpdateRequest(
	@NotBlank(message = "Tag name cannot be blank")
	@Length(min = 1, max = 10, message = "Tag name must be between 1 and 10 characters")
	String name,
	String newColor,
	@NotBlank(message = "New Tag name cannot be blank")
	@Length(min = 1, max = 10, message = "New Tag name must be between 1 and 10 characters")
	String newName) {
}
