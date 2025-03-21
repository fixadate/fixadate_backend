package com.fixadate.domain.auth.dto.response;

public record MemberSigninResponse(
	String id,
	String name,
	String nickname,
	String birth,
	String gender,
	String profession,
	String signatureColor,
	String email,
	boolean hasPushKey
) {
}
