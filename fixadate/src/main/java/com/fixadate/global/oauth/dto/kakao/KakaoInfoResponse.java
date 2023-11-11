package com.fixadate.global.oauth.dto.kakao;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class KakaoInfoResponse {
    private String id;
    private Map<String, String> properties;
    private KakaoProfile kakao_account;
    private KakaoTokenResponse kakaoTokenResponse;
}
