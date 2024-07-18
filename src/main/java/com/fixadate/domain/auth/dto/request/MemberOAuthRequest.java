package com.fixadate.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record MemberOAuthRequest(
	@NotBlank(message = "OauthId cannot be blank")
	String oauthId,
	@NotBlank(message = "Member name cannot be blank")
	String memberName,
	@Email
	@NotBlank
	String email,
	@NotBlank(message = "OauthPlatform cannot be blank")
	String oauthPlatform) {
}
