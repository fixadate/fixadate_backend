package com.fixadate.domain.auth.entity;

import static com.fixadate.global.exception.ExceptionCode.*;

import com.fixadate.global.exception.badrequest.OAuthPlatformBadRequest;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OAuthProvider {
	NAVER("NAVER"),
	KAKAO("KAKAO"),
	GOOGLE("GOOGLE"),
	APPLE("APPLE");
	private final String provider;

	public static OAuthProvider translateStringToOAuthProvider(String oauthPlatform) {
		return switch (oauthPlatform.toLowerCase()) {
			case "kakao" -> OAuthProvider.KAKAO;
			case "google" -> OAuthProvider.GOOGLE;
			case "apple" -> OAuthProvider.APPLE;
			default -> throw new OAuthPlatformBadRequest(NOT_SUPPORTED_OAUTH_SERVICE);
		};
	}
}
