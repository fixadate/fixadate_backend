package com.fixadate.global.auth.dto.request;

import jakarta.validation.constraints.NotBlank;


public record MemberOAuthRequest(
        @NotBlank String oauthId,
        @NotBlank String memberName,
        @NotBlank String email,
        @NotBlank String oauthPlatform) {
}
