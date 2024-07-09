package com.fixadate.domain.auth.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

public record MemberRegisterRequest(
	@NotBlank String oauthId,
	@NotBlank String oauthPlatform,
	@NotBlank String name,
	@NotBlank String profileImg,
	@NotBlank String nickname,
	@NotBlank String birth,
	@NotBlank String gender,
	@NotBlank String profession,
	@NotBlank String signatureColor,
	@NotBlank String email,
	@NotBlank String role
) {
	public String getProfileImg() {
		return this.profileImg + UUID.randomUUID();
	}
}
