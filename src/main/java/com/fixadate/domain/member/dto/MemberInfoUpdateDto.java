package com.fixadate.domain.member.dto;

public record MemberInfoUpdateDto(
	String memberId,
	String nickname,
	String signatureColor,
	String profession,
	String profileImg
) {
}
