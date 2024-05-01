package com.fixadate.global.auth.dto.request;

import com.fixadate.global.oauth.entity.OAuthProvider;
import jakarta.validation.constraints.NotBlank;

import static com.fixadate.global.oauth.entity.OAuthProvider.*;


public record MemberOAuthRequest(
        @NotBlank String oauthId,
        @NotBlank String memberName,
        @NotBlank String email,
        @NotBlank String oauthPlatform) {

    public OAuthProvider getOAuthProvider() {
        return translateStringToOAuthProvider(this.oauthPlatform);
    }

}
