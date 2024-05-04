package com.fixadate.global.jwt.service;

import com.fixadate.config.DataClearExtension;
import com.fixadate.global.jwt.entity.TokenResponse;
import com.fixadate.global.jwt.exception.AccessTokenBlackListException;
import com.fixadate.global.jwt.exception.RefreshTokenInvalidException;
import com.redis.testcontainers.RedisContainer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.fixadate.global.util.constant.ConstantValue.BLACK_LIST;
import static com.fixadate.global.util.constant.ConstantValue.REFRESH_TOKEN;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(DataClearExtension.class)
@SpringBootTest
@Testcontainers
class JwtProviderTest {
    private static final String SAMPLE_SUBJECT = "20240504Absegd";
    private static final Long SAMPLE_EXPIRATION_TIME = 60000L;
    private static final Long SAMPLE_EXPIRED_TIME = 0L;
    private static final String REDIS_IMAGE = "redis:5.0.7-alpine";
    private static final int REDIS_PORT = 6379;
    private static Claims claims;

    @Value("${jwt.secret}")
    private String realSecretKey;

    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Container
    static MySQLContainer mySQLContainer = new MySQLContainer<>("mysql:8.0.31");
    @Container
    static RedisContainer redisContainer = new RedisContainer(REDIS_IMAGE).withExposedPorts(REDIS_PORT);

    @BeforeAll
    static void startContainers() {
        claims = Jwts.claims();
        claims.put("id", SAMPLE_SUBJECT);
        mySQLContainer.start();
        redisContainer.start();
    }

    @AfterAll
    static void stopContainers() {
        mySQLContainer.stop();
        redisContainer.stop();
    }

    @BeforeEach
    void initRedis() {
        redisTemplate.delete(SAMPLE_SUBJECT);
    }

    private TokenResponse getTokenResponseTest() {
        return jwtProvider.getTokenResponse(SAMPLE_SUBJECT);
    }

    private Cookie makeRefreshTokenCookie(String refreshToken) {
        return new Cookie(REFRESH_TOKEN.getValue(), refreshToken);
    }

    @DisplayName("accessToken과 refreshToken을 생성할 수 있다.")
    @Test
    void generateTokenTest() {
        final TokenResponse memberTokens = getTokenResponseTest();

        assertEquals(SAMPLE_SUBJECT, jwtProvider.getIdFromToken(memberTokens.getAccessToken()));
        assertEquals(SAMPLE_SUBJECT, jwtProvider.getIdFromToken(memberTokens.getRefreshToken()));
        assertEquals(memberTokens.getRefreshToken(), redisTemplate.opsForValue().get(SAMPLE_SUBJECT));
    }

    @DisplayName("accessToken과 refreshToken이 모두 유효한 토큰일 때 검증로직을 통과한다.")
    @Test
    void validateTokenTest_Success() {
        final TokenResponse memberTokens = getTokenResponseTest();
        assertAll(
                () -> assertDoesNotThrow(() -> jwtProvider.validateToken(memberTokens.getAccessToken())),
                () -> assertDoesNotThrow(() -> jwtProvider.validateToken(memberTokens.getRefreshToken())),
                () -> assertTrue(jwtProvider.validateToken(memberTokens.getAccessToken())),
                () -> assertTrue(jwtProvider.validateToken(memberTokens.getRefreshToken()))
        );
    }

    @DisplayName("토큰의 기한이 만료되었을 때 예외 처리한다")
    @Test
    void validateTokenTestIfTokenIsExpired() {
        final String refreshToken = jwtProvider.createToken(claims, SAMPLE_EXPIRED_TIME);
        final String accessToken = jwtProvider.createToken(claims, SAMPLE_EXPIRED_TIME);

        assertAll(
                () -> assertThrows(ExpiredJwtException.class, () -> jwtProvider.validateToken(accessToken)),
                () -> assertThrows(ExpiredJwtException.class, () -> jwtProvider.validateToken(refreshToken))
        );
    }

    @Nested
    @DisplayName("토큰 재발급 테스트")
    class reIssueToken {
        @DisplayName("RefreshToken이 만료되지 않으면 토큰을 재발급한다.")
        @Test
        void reIssueToken_Success() {
            final String refreshToken = jwtProvider.createRefreshToken(SAMPLE_SUBJECT);

            Cookie cookie = makeRefreshTokenCookie(refreshToken);
            assertAll(
                    () -> assertDoesNotThrow(() -> jwtProvider.reIssueToken(cookie)),
                    () -> assertNotNull(redisTemplate.opsForValue().get(SAMPLE_SUBJECT)),
                    () -> assertTrue(jwtProvider.validateToken(redisTemplate.opsForValue().get(SAMPLE_SUBJECT)))
            );
        }

        @DisplayName("RefreshToken이 만료되면 재발급 하지 않고 에러를 반환한다..")
        @Test
        void reIssueTokenIfRefreshTokenExpired() {
            final String refreshToken = jwtProvider.createToken(claims, SAMPLE_EXPIRED_TIME);
            Cookie cookie = makeRefreshTokenCookie(refreshToken);
            assertAll(
                    () -> assertThrows(ExpiredJwtException.class, () -> jwtProvider.reIssueToken(cookie)),
                    () -> assertThrows(ExpiredJwtException.class, () -> jwtProvider.validateToken(refreshToken))
            );
        }

        @DisplayName("redis에 RefreshToken이 존재하지 않으면 재발급 하지 않고 에러를 반환한다.")
        @Test
        void reIssueTokenIfRefreshTokenNotExistsInRedis() {
            final String refreshToken = jwtProvider.createToken(claims, SAMPLE_EXPIRATION_TIME);
            Cookie cookie = makeRefreshTokenCookie(refreshToken);

            assertAll(
                    () -> assertThrows(RefreshTokenInvalidException.class, () -> jwtProvider.reIssueToken(cookie)),
                    () -> assertNull(redisTemplate.opsForValue().get(SAMPLE_SUBJECT))
            );
        }
    }

    @DisplayName("블랙 리스트 등록 테스트")
    @Test
    void setAccessTokenBlackList() {
        final String accessToken = jwtProvider.createToken(claims, SAMPLE_EXPIRED_TIME);
        jwtProvider.setAccessTokenBlackList(accessToken);

        assertNotNull(redisTemplate.opsForValue().get(accessToken));
    }

    @DisplayName("로그아웃 테스트")
    @Test
    void memberLogoutTest() {
        TokenResponse tokenResponse = jwtProvider.getTokenResponse(SAMPLE_SUBJECT);
        jwtProvider.memberLogout(tokenResponse.getAccessToken());

        assertAll(
                () -> assertNotNull(redisTemplate.opsForValue().get(tokenResponse.getAccessToken())),
                () -> assertNull(redisTemplate.opsForValue().get(SAMPLE_SUBJECT)),
                () -> assertEquals(BLACK_LIST.getValue(), redisTemplate.opsForValue().get(tokenResponse.getAccessToken())),
                () -> assertThatThrownBy(() -> jwtProvider.isTokenBlackList(tokenResponse.getAccessToken()))
                        .isInstanceOf(AccessTokenBlackListException.class)
                        .hasMessage("AccessToken is in BlackList")
        );
    }
}