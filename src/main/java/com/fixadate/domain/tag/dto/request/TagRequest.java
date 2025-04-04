package com.fixadate.domain.tag.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record TagRequest(
	@NotBlank(message = "Tag color cannot be blank")
	String color,
	@NotBlank(message = "Tag name cannot be blank")
	@Length(min = 1, max = 10, message = "Tag name must be between 1 and 10 characters")
	String name) {
}
