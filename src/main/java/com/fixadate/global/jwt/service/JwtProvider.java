package com.fixadate.global.jwt.service;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.exception.MemberNotFoundException;
import com.fixadate.domain.member.repository.MemberRepository;
import com.fixadate.global.jwt.MemberPrincipal;
import com.fixadate.global.jwt.exception.TokenException;
import com.fixadate.global.jwt.exception.TokenExpiredException;
import com.fixadate.global.jwt.exception.TokenUnsupportedException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {
    private final MemberRepository memberRepository;
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.accessToken.expiration-period}")
    private int accesesTokenexpirationPeriod;
    @Value("${jwt.refreshToken.expiration-period}")
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

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secret.getBytes())
                    .parseClaimsJws(token)
                    .getBody();
            return true;
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException();
        } catch (UnsupportedJwtException e) {
            throw new TokenUnsupportedException();
        } catch (Exception e) {
            throw new TokenException();
        }
    }

    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        String oauthId = getOauthIdFromToken(token);
        Member member = memberRepository.findMemberByOauthId(oauthId).orElseThrow(TokenException::new);
        MemberPrincipal memberPrincipal = new MemberPrincipal(member);
        return new UsernamePasswordAuthenticationToken(memberPrincipal, token, memberPrincipal.getAuthorities());
    }
    private String getOauthIdFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret.getBytes())
                .parseClaimsJws(token)
                .getBody()
                .get("oauthId",
                        String.class);
    }
}
