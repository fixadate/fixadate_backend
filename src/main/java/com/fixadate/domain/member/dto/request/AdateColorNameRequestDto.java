package com.fixadate.domain.member.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AdateColorNameRequestDto(@NotBlank String color, @NotBlank String name) {
}
