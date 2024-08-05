package com.fixadate.domain.member.dto.request;

public record MemberInfoUpdateRequest(
	String nickname,
	String signatureColor,
	String profession,
	String profileImg
) {
}
