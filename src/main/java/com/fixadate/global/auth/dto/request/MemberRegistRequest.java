package com.fixadate.global.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record MemberRegistRequest(
	@NotBlank String oauthId,
	@NotBlank String oauthPlatform,
	@NotBlank String name,
	@NotBlank String profileImg,
	@NotBlank String nickname,
	@NotBlank String birth,
	@NotBlank String gender,
	@NotBlank String profession,
	@NotBlank String signatureColor,
	@NotBlank String contentType,
	@NotBlank String email,
	@NotBlank String role
) {
}
