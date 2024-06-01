package com.fixadate.global.auth.dto.request;

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
}
