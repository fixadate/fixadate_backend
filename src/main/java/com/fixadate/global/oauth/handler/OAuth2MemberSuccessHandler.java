package com.fixadate.global.oauth.handler;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

@RequiredArgsConstructor
@Component
@Slf4j
public class OAuth2MemberSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final OAuth2AuthorizedClientService authorizedClientService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            // OAuth2AuthorizedClient를 통해 사용자의 클라이언트 정보 가져오기
            OAuth2AuthorizedClient authorizedClient = getAuthorizedClient(oauthToken);
            OAuth2User oAuth2User = getOAuth2UserFromToken(oauthToken);
            OAuth2AccessToken oAuth2AccessToken = authorizedClient.getAccessToken();
            redirect(request, response, oAuth2User, oAuth2AccessToken);
        }
    }

    // OAuth2AuthorizedClient 가져오기
    private OAuth2AuthorizedClient getAuthorizedClient(OAuth2AuthenticationToken oauthToken) {
        String clientRegistrationId = oauthToken.getAuthorizedClientRegistrationId();
        return authorizedClientService.loadAuthorizedClient(
                clientRegistrationId, oauthToken.getName());
    }

    private void redirect(HttpServletRequest request, HttpServletResponse response,
                          OAuth2User oAuth2User, OAuth2AccessToken oAuth2AccessToken)
            throws IOException {
        String email = oAuth2User.getAttribute("email");
        log.info("여기야!");
        String uri = createURI(oAuth2AccessToken, email).toString(); // Access Token과 Refresh Token을 포함한 URL을 생성
        getRedirectStrategy().sendRedirect(request, response, uri);
    }

    private OAuth2User getOAuth2UserFromToken(OAuth2AuthenticationToken token) {
        return token.getPrincipal();
    }

    // Redirect URI 생성. JWT를 쿼리 파라미터로 담아 전달한다.
    private URI createURI(OAuth2AccessToken oAuth2AccessToken, String email) {
        return UriComponentsBuilder
                .newInstance()
                .scheme("https")
                .host("api.fixadate.app")
                .path("/hello")
                .queryParam("accessToken", oAuth2AccessToken.getTokenValue())
                .queryParam("email", email)
                .build()
                .toUri();
    }
}