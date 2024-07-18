package com.fixadate.domain.auth.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record MemberRegisterRequest(
	@NotBlank(message = "OauthId cannot be blank")
	String oauthId,
	@NotBlank(message = "OauthPlatform cannot be blank")
	String oauthPlatform,
	@NotBlank(message = "Name cannot be blank")
	String name,
	@NotBlank(message = "Profile image cannot be blank")
	String profileImg,
	@NotBlank(message = "Nickname cannot be blank")
	String nickname,
	@NotBlank(message = "Birth cannot be blank")
	String birth,
	@NotBlank(message = "Gender cannot be blank")
	String gender,
	@NotBlank(message = "Profession cannot be blank")
	String profession,
	@NotBlank(message = "Signature color cannot be blank")
	String signatureColor,
	@NotBlank(message = "Email cannot be blank")
	@Email
	String email,
	@NotBlank(message = "Role cannot be blank")
	String role
) {
	public String getProfileImg() {
		return this.profileImg + UUID.randomUUID();
	}
}
