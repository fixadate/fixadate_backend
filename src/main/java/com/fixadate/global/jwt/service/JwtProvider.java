package com.fixadate.global.jwt.service;

import static com.fixadate.global.exception.ExceptionCode.INVALID_TOKEN_BLACKLIST;
import static com.fixadate.global.exception.ExceptionCode.NOT_FOUND_MEMBER_IDENTIFIER;
import static com.fixadate.global.exception.ExceptionCode.NOT_FOUND_REFRESHTOKEN;
import static com.fixadate.global.util.constant.ConstantValue.AUTHORIZATION;
import static com.fixadate.global.util.constant.ConstantValue.AUTHORIZATION_BEARER;
import static com.fixadate.global.util.constant.ConstantValue.BLACK_LIST;
import static com.fixadate.global.util.constant.ConstantValue.ID;

import java.security.Key;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.service.repository.MemberRepository;
import com.fixadate.global.exception.unauthorized.TokenException;
import com.fixadate.global.jwt.MemberPrincipal;
import com.fixadate.global.jwt.entity.TokenResponse;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {
	@Value("${jwt.secret}")
	private String secret;
	@Value("${jwt.accessToken.expiration-period}")
	private long accesesTokenexpirationPeriod;
	@Value("${jwt.refreshToken.expiration-period}")
	private int refreshTokenexpirationPeriod;

	private final RedisTemplate<String, String> redisTemplate;
	private final MemberRepository memberRepository;

	private Key key;

	@PostConstruct
	private void init() {
		key = Keys.hmacShaKeyFor(secret.getBytes());
	}

	public TokenResponse getTokenResponse(String id) {
		return new TokenResponse(createAccessToken(id), createRefreshToken(id));
	}

	public String createAccessToken(String id) {
		Claims claims = Jwts.claims();
		claims.put(ID.getValue(), id);
		return createToken(claims, accesesTokenexpirationPeriod);
	}

	public String createRefreshToken(String id) {
		Claims claims = Jwts.claims();
		claims.put(ID.getValue(), id);
		String refreshToken = createToken(claims, refreshTokenexpirationPeriod);
		registRefreshTokenInRedis(refreshToken, id);
		return refreshToken;
	}

	private void registRefreshTokenInRedis(String refreshToken, String id) {
		redisTemplate.delete(id);
		redisTemplate.opsForValue().set(id, refreshToken, Duration.ofDays(90));
	}

	public TokenResponse reIssueToken(Cookie cookie) {
		String refreshToken = cookie.getValue();
		String id = getIdFromToken(refreshToken);

		String oldRefreshToken = redisTemplate.opsForValue().get(id);
		if (oldRefreshToken == null || !oldRefreshToken.equals(refreshToken)) {
			throw new TokenException(NOT_FOUND_REFRESHTOKEN);
		}
		redisTemplate.delete(id);
		return getTokenResponse(id);
	}

	public void setAccessTokenBlackList(String accessToken) {
		redisTemplate.opsForValue().set(accessToken, BLACK_LIST.getValue(), Duration.ofDays(2));
	}

	public void memberLogout(String accessToken) {
		String id = getIdFromToken(accessToken);
		setAccessTokenBlackList(accessToken);
		redisTemplate.delete(id);
	}

	public boolean isTokenBlackList(String accessToken) {
		String value = redisTemplate.opsForValue().get(accessToken);
		if (value != null && value.equals(BLACK_LIST.getValue())) {
			throw new TokenException(INVALID_TOKEN_BLACKLIST);
		}
		return false;
	}

	public String createToken(Claims claims, long expirationPeriod) {
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
		String id = getIdFromToken(token);
		Member member = memberRepository.findMemberById(id).orElseThrow(() ->
																			new TokenException(
																				NOT_FOUND_MEMBER_IDENTIFIER));
		MemberPrincipal memberPrincipal = new MemberPrincipal(member);
		return new UsernamePasswordAuthenticationToken(memberPrincipal, token, memberPrincipal.getAuthorities());
	}

	public String retrieveToken(HttpServletRequest httpServletRequest) {
		String bearerToken = httpServletRequest.getHeader(AUTHORIZATION.getValue());
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(AUTHORIZATION_BEARER.getValue())) {
			return bearerToken.substring(7);
		}
		return null;
	}

	public String getIdFromToken(String token) {
		return Jwts.parser()
				   .setSigningKey(secret.getBytes())
				   .parseClaimsJws(token)
				   .getBody()
				   .get(ID.getValue(), String.class);
	}
}
