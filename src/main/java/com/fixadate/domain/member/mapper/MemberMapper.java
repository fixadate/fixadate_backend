package com.fixadate.domain.member.mapper;

import static com.fixadate.domain.auth.entity.OAuthProvider.translateStringToOAuthProvider;

import com.fixadate.domain.auth.dto.request.MemberRegisterRequest;
import com.fixadate.domain.auth.dto.response.MemberSigninResponse;
import com.fixadate.domain.member.dto.response.MemberInfoResponse;
import com.fixadate.domain.member.entity.Member;

public class MemberMapper {

	private MemberMapper() {
	}

	public static Member toEntity(final MemberRegisterRequest request, final String oauthId) {
		return Member.builder()
					 .oauthId(oauthId)
					 .oauthPlatform(translateStringToOAuthProvider(request.oauthPlatform()))
					 .name(request.name())
					 .profileImg(request.getProfileImg())
					 .nickname(request.nickname())
					 .birth(request.birth())
					 .gender(request.gender())
					 .email(request.email())
					 .profession(request.profession())
					 .signatureColor(request.signatureColor())
					 .role(request.role())
					 .build();

	}

	public static MemberSigninResponse toResponse(final Member member) {
		return new MemberSigninResponse(
			member.getId(),
			member.getName(),
			member.getNickname(),
			member.getBirth(),
			member.getGender(),
			member.getProfession(),
			member.getSignatureColor(),
			member.getEmail()
		);
	}

	public static MemberInfoResponse toInfoResponse(final Member member, final String url) {
		return new MemberInfoResponse(
			member.getName(),
			member.getNickname(),
			member.getBirth(),
			member.getGender(),
			member.getSignatureColor(),
			member.getProfession(),
			url
		);
	}
}
