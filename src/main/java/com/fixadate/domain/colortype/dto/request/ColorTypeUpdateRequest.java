package com.fixadate.domain.colortype.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ColorTypeUpdateRequest(@NotBlank String color, @NotBlank String name, @NotBlank String newColor, @NotBlank String newName) {
}
