package com.fixadate.global.oauth.entity;

import lombok.Getter;

@Getter
public enum OAuthProvider {
    Naver("NAVER"),
    Kakao("KAKAO"),
    Google("GOOGLE"),
    apple("APPLE");
    private final String provider;

    OAuthProvider(String provider) {
        this.provider = provider;
    }

}
