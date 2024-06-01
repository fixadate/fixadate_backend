package com.fixadate.global.auth.dto.response;

public record MemberSigninResponse(
	String id,
	String name,
	String nickname,
	Integer birth,
	String gender,
	String profession,
	String signatureColor,
	String email
) {
}
