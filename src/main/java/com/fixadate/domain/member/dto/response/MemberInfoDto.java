package com.fixadate.domain.member.dto.response;

import com.fixadate.domain.auth.entity.OAuthProvider;
import com.fixadate.domain.googlecalendar.entity.GoogleCredentials;
import com.fixadate.domain.pushkey.entity.PushKey;

public record MemberInfoDto(
	OAuthProvider oAuthProvider,
	String name,
	String profileImg,
	String nickname,
	String birth,
	String gender,
	String profession,
	String signatureColor,
	String email,
	PushKey pushKey,
	GoogleCredentials googleCredentials,
	String url
) {
}
