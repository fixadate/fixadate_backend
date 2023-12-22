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
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthorizationCodeAuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
@Component
@Slf4j
public class OAuth2MemberSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final OAuth2AuthorizedClientService authorizedClientService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

            // OAuth2AuthorizedClient를 통해 사용자의 클라이언트 정보 가져오기
            OAuth2AuthorizedClient authorizedClient = getAuthorizedClient(oauthToken);
            OAuth2AccessToken oAuth2AccessToken = authorizedClient.getAccessToken();

            log.info(authorizedClient.getAccessToken().getTokenValue());

            redirect(request, response, oAuth2AccessToken);
        }
    }

    // OAuth2AuthorizedClient 가져오기
    private OAuth2AuthorizedClient getAuthorizedClient(OAuth2AuthenticationToken oauthToken) {
        String clientRegistrationId = oauthToken.getAuthorizedClientRegistrationId();
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                clientRegistrationId, oauthToken.getName());
        return authorizedClient;
    }


    private void redirect(HttpServletRequest request, HttpServletResponse response,
                          OAuth2AccessToken oAuth2AccessToken)
            throws IOException {
        String uri = createURI(oAuth2AccessToken).toString(); // Access Token과 Refresh Token을 포함한 URL을 생성

        getRedirectStrategy().sendRedirect(request, response, uri);
    }


    // Redirect URI 생성. JWT를 쿼리 파라미터로 담아 전달한다.
    private URI createURI(OAuth2AccessToken oAuth2AccessToken) {
        return UriComponentsBuilder
                .newInstance()
                .scheme("http")
                .host("localhost")
                .port(8080)
                .path("/googleCalendar")
                .queryParam("accessToken", oAuth2AccessToken.getTokenValue())
                .build()
                .toUri();
    }
}