package com.fixadate.global.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


public record MemberOAuthRequestDto(
        @NotBlank String oauthId) {
}
