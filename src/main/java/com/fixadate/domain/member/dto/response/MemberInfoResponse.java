package com.fixadate.domain.member.dto.response;

public record MemberInfoResponse(
	String name,
	String nickname,
	String birth,
	String gender,
	String signatureColor,
	String profession,
	String url
) {
}
