package com.fixadate.domain.oauth.service;

import lombok.RequiredArgsConstructor;s
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OauthService { //GOOGLE, NAVER, KAKAO, APPLE 순
    @Value("${google.client.id}")
    private final String GOOGLE_CLIENT_ID;
    @Value("${google.client.pw}")
    private final String GOOGLE_CLIENT_PW;
    @Value("${naver.client.id}")
    private final String NAVER_CLIENT_ID;
    @Value("${naver.client.pw}")
    private final String NAVER_CLIENT_PW;
    @Value("${google.client.redirect_uri}")
    private final String GOOGLE_CLIENT_REDIRECT_URI;
    @Value("${naver.client.redirect_uri}")
    private final String NAVER_CLIENT_REDIRECT_URI;
    @Value("${kakao.client.id}")
    private final String KAKAO_CLIENT_ID;
    @Value("${kakao.client.redirect_uri}")
    private final String KAKAO_REDIRECT_URI;

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
}
