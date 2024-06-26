package com.fixadate.domain.member.mapper;

import static com.fixadate.global.auth.entity.OAuthProvider.*;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.auth.dto.request.MemberRegistRequest;
import com.fixadate.global.auth.dto.response.MemberSigninResponse;

/**
 *
 * @author yongjunhong
 * @since 2024. 6. 1.
 */
public class MemberMapper {

	private MemberMapper() {
	}

	public static Member toEntity(MemberRegistRequest request, String oauthId) {
		return Member.builder()
			.oauthId(oauthId)
			.oauthPlatform(translateStringToOAuthProvider(request.oauthPlatform()))
			.name(request.name())
			.profileImg(request.profileImg())
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
}
