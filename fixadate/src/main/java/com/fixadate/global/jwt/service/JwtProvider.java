package com.fixadate.global.jwt.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;

public class JwtProvider {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.accessToken.expiration-period")
    private int accesesTokenexpirationPeriod;
    @Value("${jwt.refreshToken.expiration-period")
    private int refreshTokenexpirationPeriod;

    private Key key;

    @PostConstruct
    private void init() {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String createAccessToken(String oauthId) {
        Claims claims = Jwts.claims();
        claims.put("oauthId", oauthId);
        return createToken(claims, accesesTokenexpirationPeriod);
    }
    public String createRefreshToken(String oauthId) {
        Claims claims = Jwts.claims();
        claims.put("oauthId", oauthId);
        return createToken(claims, refreshTokenexpirationPeriod);
    }

    private String createToken(Claims claims, int expirationPeriod) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(issuedAt())
                .setExpiration(expiredAt(expirationPeriod))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private Date issuedAt() {
        LocalDateTime now = LocalDateTime.now();
        return Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
    }

    private Date expiredAt(int expirationPeriod) {
        LocalDateTime now = LocalDateTime.now();
        return Date.from(now.plusHours(expirationPeriod).atZone(ZoneId.systemDefault()).toInstant());
    }
}
