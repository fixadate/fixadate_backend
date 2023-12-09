package com.fixadate.domain.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdateColorNameRequestDto {
    @NotBlank
    private String color;
    @NotBlank
    private String name;
}
