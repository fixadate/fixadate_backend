package com.fixadate.global.jwt.service;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.exception.MemberNotFoundException;
import com.fixadate.domain.member.repository.MemberRepository;
import com.fixadate.global.jwt.MemberPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {
    private final MemberRepository memberRepository;
    static final String OAUTH_ID = "oauthId";
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.accessToken.expiration-period}")
    private long accesesTokenexpirationPeriod;
    @Value("${jwt.refreshToken.expiration-period}")
    private int refreshTokenexpirationPeriod;
    private Key key;

    @PostConstruct
    private void init() {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String createAccessToken(String oauthId) {
        Claims claims = Jwts.claims();
        claims.put(OAUTH_ID, oauthId);
        return createToken(claims, accesesTokenexpirationPeriod);
    }

    public String createRefreshToken(String oauthId) {
        Claims claims = Jwts.claims();
        claims.put(OAUTH_ID, oauthId);
        return createToken(claims, refreshTokenexpirationPeriod);
    }

    private String createToken(Claims claims, long expirationPeriod) {
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

    private Date expiredAt(long expirationPeriod) {
        LocalDateTime now = LocalDateTime.now();
        return Date.from(now.plusSeconds(expirationPeriod).atZone(ZoneId.systemDefault()).toInstant());
    }

    public boolean validateToken(String token) {
        Jwts.parser()
                .setSigningKey(secret.getBytes())
                .parseClaimsJws(token)
                .getBody();
        return true;
    }

    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        String oauthId = getOauthIdFromToken(token);
        //fixme 20240221 token으로 member를 찾지 못 할때 예외처리 구현해야함.
        Member member = memberRepository.findMemberByOauthId(oauthId).orElseThrow(MemberNotFoundException::new);
        MemberPrincipal memberPrincipal = new MemberPrincipal(member);
        return new UsernamePasswordAuthenticationToken(memberPrincipal, token, memberPrincipal.getAuthorities());
    }

    public String getOauthIdFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret.getBytes())
                .parseClaimsJws(token)
                .getBody()
                .get(OAUTH_ID,
                        String.class);
    }
}
