package com.fixadate.global.oauth.dto.kakao;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KakaoTokenRequestViaCode {
    private String grantType;
    private String clientId;
    private String redirectUri;
    private String code;
}
