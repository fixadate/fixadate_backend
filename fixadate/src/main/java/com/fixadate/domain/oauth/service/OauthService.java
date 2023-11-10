package com.fixadate.domain.oauth.service;

import com.fixadate.domain.oauth.dto.kakao.KakaoInfoResponse;
import com.fixadate.domain.oauth.dto.kakao.KakaoTokenRequestViaCode;
import com.fixadate.domain.oauth.dto.kakao.KakaoTokenResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
@RequiredArgsConstructor
public class OauthService {
    //GOOGLE, NAVER, KAKAO, APPLE 순
    @Value("${google.client.id}")
    private String GOOGLE_CLIENT_ID;
    @Value("${google.client.pw}")
    private String GOOGLE_CLIENT_PW;
    @Value("${naver.client.id}")
    private String NAVER_CLIENT_ID;
    @Value("${naver.client.pw}")
    private String NAVER_CLIENT_PW;
    @Value("${google.client.redirect_uri}")
    private String GOOGLE_CLIENT_REDIRECT_URI;
    @Value("${naver.client.redirect_uri}")
    private String NAVER_CLIENT_REDIRECT_URI;
    @Value("${kakao.client.id}")
    private String KAKAO_CLIENT_ID;
    @Value("${kakao.client.redirect_uri}")
    private String KAKAO_REDIRECT_URI;

    public String createGoogleLoginUri() {
        return "https://accounts.google.com/o/oauth2/v2/auth?client_id=" + GOOGLE_CLIENT_ID
                + "&redirect_uri=" + GOOGLE_CLIENT_REDIRECT_URI
                + "&response_type=code&scope=email%20profile%20openid&access_type=offline";
    }

    public String createNaverLoginUri() {
        return "https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=" + NAVER_CLIENT_ID
                + "&redirect_uri=" + NAVER_CLIENT_REDIRECT_URI;
    }

    public String createKakaoLoginUri() {
        return "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=" + KAKAO_CLIENT_ID
                + "&redirect_uri=" + KAKAO_REDIRECT_URI;
    }

    public KakaoTokenResponse kakaoIssueTokensViaCode(String authCode) {
        RestTemplate restTemplate = new RestTemplate();

        KakaoTokenRequestViaCode kakaoTokenRequestParam = KakaoTokenRequestViaCode
                .builder()
                .grantType("authorization_code")
                .clientId(KAKAO_CLIENT_ID)
                .redirectUri(KAKAO_REDIRECT_URI)
                .code(authCode)
                .build();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", KAKAO_CLIENT_ID);
        body.add("redirect_uri", KAKAO_REDIRECT_URI);
        body.add("code", authCode);

        HttpHeaders headers = new HttpHeaders();

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, headers); //dto to MultiValueMap

        ResponseEntity<KakaoTokenResponse> kakaoTokenResponse = restTemplate.postForEntity(
                "https://kauth.kakao.com/oauth/token", httpEntity, KakaoTokenResponse.class);

        return kakaoTokenResponse.getBody();
    }

    public KakaoInfoResponse kakaoGetUserInfo(KakaoTokenResponse kakaoTokenResponse) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+kakaoTokenResponse.getAccess_token());

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
//        String result = String.valueOf(restTemplate.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.GET, httpEntity, String.class));
//        System.out.println(result);
        ResponseEntity<KakaoInfoResponse> kakaoInfoResponse = restTemplate.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.GET, httpEntity, KakaoInfoResponse.class);
        kakaoInfoResponse.getBody().setKakaoTokenResponse(kakaoTokenResponse);

        return kakaoInfoResponse.getBody();
    }
}
