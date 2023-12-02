package com.fixadate.global.oauth.entity;

public enum OAuthProvider {
    Naver("NAVER"),
    Kakao("KAKAO"),
    Google("GOOGLE");
    private final String provider;

    OAuthProvider(String provider) {
        this.provider = provider;
    }

    public String getProvider() {
        return provider;
    }
}
