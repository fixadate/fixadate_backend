package com.fixadate.domain.oauth.controller;

import com.fixadate.domain.oauth.service.OauthService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OauthController {
    private final OauthService oauthService;

    @GetMapping("/oauth/kakao/login/uri")
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
}
