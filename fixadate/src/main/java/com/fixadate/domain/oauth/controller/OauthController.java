package com.fixadate.domain.oauth.controller;

import com.fixadate.domain.oauth.dto.kakao.KakaoInfoResponse;
import com.fixadate.domain.oauth.dto.kakao.KakaoTokenResponse;
import com.fixadate.domain.oauth.service.OauthService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OauthController {
    private final OauthService oauthService;

    @GetMapping("/oauth/google/login/uri")
    public String getGoogleLoginUri() {
        return oauthService.createGoogleLoginUri();
    }

    @GetMapping("/oauth/naver/login/uri")
    public String getNaverLoginUri() {
        return oauthService.createNaverLoginUri();
    }

    @GetMapping("/oauth/kakao/login/uri")
    public String getKakaoLoginUri() {
        return oauthService.createKakaoLoginUri();
    }

    @GetMapping("/oauth/kakao/login/redirect")
    public KakaoInfoResponse getKakaoUserInfoByCode(@RequestParam(value = "code") String authCode) {
        KakaoTokenResponse kakaoTokenResponse = oauthService.kakaoIssueTokensViaCode(authCode);
        return oauthService.kakaoGetUserInfo(kakaoTokenResponse);
    }
}
