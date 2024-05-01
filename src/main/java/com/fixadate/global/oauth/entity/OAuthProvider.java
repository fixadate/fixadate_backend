package com.fixadate.global.oauth.entity;

import com.fixadate.global.auth.exception.UnknownOAuthPlatformException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OAuthProvider {
    NAVER("NAVER"),
    KAKAO("KAKAO"),
    GOOGLE("GOOGLE"),
    APPLE("APPLE");
    private final String provider;

    public static OAuthProvider translateStringToOAuthProvider(String oauthPlatform) {
        return switch (oauthPlatform.toLowerCase()) {
            case "kakao" -> OAuthProvider.KAKAO;
            case "google" -> OAuthProvider.GOOGLE;
            case "apple" -> OAuthProvider.APPLE;
            default -> throw new UnknownOAuthPlatformException();
        };
    }
}
