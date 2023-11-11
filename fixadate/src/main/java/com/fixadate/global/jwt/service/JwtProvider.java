package com.fixadate.global.jwt.service;

import jakarta.annotation.PostConstruct;
import java.security.Key;
import org.springframework.beans.factory.annotation.Value;

public class JwtProvider {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.accessToken.expiration-period")
    private int AccesesTokenexpirationPeriod;
    @Value("${jwt.refreshToken.expiration-period")
    private int RefreshTokenexpirationPeriod;

    private Key key;
    @PostConstruct
    private void init() {
        key = Keys
    }
}
