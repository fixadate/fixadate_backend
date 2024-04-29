package com.fixadate.global.auth.dto.request;

import jakarta.validation.constraints.NotBlank;


public record MemberOAuthRequest(
        @NotBlank String oauthId) {
}
