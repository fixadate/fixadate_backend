package com.fixadate.domain.member.mapper;

import static com.fixadate.domain.auth.entity.OAuthProvider.*;

import com.fixadate.domain.auth.dto.request.MemberRegisterRequest;
import com.fixadate.domain.auth.dto.response.MemberSigninResponse;
import com.fixadate.domain.member.dto.response.MemberInfoResponse;
import com.fixadate.domain.member.entity.Member;

/**
 *
 * @author yongjunhong
 * @since 2024. 6. 1.
 */
public class MemberMapper {

	private MemberMapper() {
	}

	public static Member toEntity(MemberRegisterRequest request, String oauthId) {
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

	public static MemberSigninResponse toResponse(Member member) {
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

	public static MemberInfoResponse toInfoResponse(Member member, String url) {
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
