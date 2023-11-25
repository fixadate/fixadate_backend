package com.fixadate.global.oauth.handler;


import static com.fixadate.global.oauth.ConstantValue.ACCESS_TOKEN;
import static com.fixadate.global.oauth.ConstantValue.REFRESH_TOKEN;

import com.fixadate.global.jwt.service.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
@Slf4j
public class OAuth2MemberSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String oAuthId = getOAuthIdFromOAuth2User(oAuth2User);
        redirect(request, response, oAuthId);
    }

    private String getOAuthIdFromOAuth2User(OAuth2User oAuth2User) {
        String oauthId = oAuth2User.getName();
        if (oauthId.contains("id=")) {
            // kakao, google과 다르게 naver는 getName에 json으로 값들을 보내기 때문에 처리 과정이 필요함
            return oauthId.lines()
                    .filter(line -> line.contains("id="))
                    .map(line -> line.substring(line.indexOf("id=") + 3, line.indexOf(",", line.indexOf("id="))))
                    .findFirst()
                    .orElse(oauthId.substring(oauthId.indexOf("id=") + 3));
        }
        return oauthId;
    }


    private void redirect(HttpServletRequest request, HttpServletResponse response,
                          String oAuthId) throws IOException {
        String accessToken = jwtProvider.createAccessToken(oAuthId);  // Access Token 생성
        String refreshToken = jwtProvider.createRefreshToken(oAuthId);   // Refresh Token 생성
        Cookie cookie = createHttpOnlyCookie(refreshToken);

        response.addHeader(ACCESS_TOKEN, accessToken);
        response.addCookie(cookie);
        String uri = createURI().toString(); // Access Token과 Refresh Token을 포함한 URL을 생성

        log.info(accessToken);
        log.info(refreshToken);
        getRedirectStrategy().sendRedirect(request, response, uri);
    }

    private Cookie createHttpOnlyCookie(String refreshToken) {
        Cookie cookie = new Cookie(REFRESH_TOKEN, refreshToken);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        return cookie;
    }

    // Redirect URI 생성. JWT를 쿼리 파라미터로 담아 전달한다.
    private URI createURI() {
        return UriComponentsBuilder
                .newInstance()
                .scheme("http")
                .host("localhost")
                .path("")
                .build()
                .toUri();
    }
}