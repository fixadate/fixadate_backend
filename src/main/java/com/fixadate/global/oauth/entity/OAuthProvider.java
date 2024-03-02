package com.fixadate.global.oauth.entity;

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
}
