package com.fixadate.domain.invitation.dto.request;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fixadate.domain.invitation.entity.Invitation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InvitationRequest(
	@NotBlank String memberRole,
	@NotNull long teamId) {
	public Invitation toEntity() {
		return Invitation.builder()
			.id(UUID.randomUUID().toString())
			.role(memberRole)
			.teamId(teamId)
			.userSpecify(false)
			.expiration(259200L) // 3일
			.expirationDate(getExpiration())
			.memberName(null)
			.build();
	}

	private LocalDateTime getExpiration() {
		LocalDateTime now = LocalDateTime.now();
		return now.plusDays(7);
	}
}
