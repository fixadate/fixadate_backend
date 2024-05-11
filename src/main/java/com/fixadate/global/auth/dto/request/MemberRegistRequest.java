package com.fixadate.global.auth.dto.request;

import static com.fixadate.global.oauth.entity.OAuthProvider.*;

import com.fixadate.domain.member.entity.Member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MemberRegistRequest(
	@NotBlank String oauthId,
	@NotBlank String oauthPlatform,
	@NotBlank String name,
	@NotBlank String profileImg,
	@NotBlank String nickname,
	@NotNull int birth,
	@NotBlank String gender,
	@NotBlank String profession,
	@NotBlank String signatureColor,
	@NotBlank String contentType,
	@NotBlank String email,
	@NotBlank String role
) {

	public Member of(String encodedOauthId) {
		return Member.builder()
			.oauthId(encodedOauthId)
			.oauthPlatform(translateStringToOAuthProvider(oauthPlatform))
			.name(name)
			.profileImg(profileImg)
			.nickname(nickname)
			.birth(birth)
			.gender(gender)
			.email(email)
			.profession(profession)
			.signatureColor(signatureColor)
			.profileImg(profileImg)
			.role(role)
			.build();
	}
}
