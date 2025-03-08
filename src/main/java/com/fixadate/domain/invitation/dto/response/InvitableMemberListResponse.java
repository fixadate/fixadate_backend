package com.fixadate.domain.invitation.dto.response;

import com.fixadate.domain.member.entity.Member;

public record InvitableMemberListResponse(
		String memberId,
		String email,
		String name,
		String profileImageUrl
	){
	public static InvitableMemberListResponse of(Member member){
		return new InvitableMemberListResponse(
			member.getId(),
			member.getEmail(),
			member.getName(),
			member.getProfileImg()
		);
	}
}