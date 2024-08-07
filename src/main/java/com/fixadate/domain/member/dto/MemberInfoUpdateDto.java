package com.fixadate.domain.member.dto;

import com.fixadate.domain.member.dto.request.MemberInfoUpdateRequest;

public record MemberInfoUpdateDto(
	String memberId,
	String nickname,
	String signatureColor,
	String profession,
	String profileImg
) {

	public static MemberInfoUpdateDto of(final String memberId, final MemberInfoUpdateRequest memberInfoUpdateRequest) {
		return new MemberInfoUpdateDto(
			memberId,
			memberInfoUpdateRequest.nickname(),
			memberInfoUpdateRequest.signatureColor(),
			memberInfoUpdateRequest.profession(),
			memberInfoUpdateRequest.profession()
		);
	}
}
