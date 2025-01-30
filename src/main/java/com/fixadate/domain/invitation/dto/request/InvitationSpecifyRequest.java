package com.fixadate.domain.invitation.dto.request;

import com.fixadate.domain.member.entity.Member;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fixadate.domain.invitation.entity.Invitation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InvitationSpecifyRequest(
	@NotBlank String receiverId,
	@NotBlank String memberName,
	@NotBlank String memberRole,
	@NotNull long teamId) {

	public Invitation toEntity(Member sender, Member receiver) {
		return Invitation.builder()
			.id(UUID.randomUUID().toString())
			.sender(sender)
			.receiver(receiver)
			.senderName(sender.getName())
			.receiverName(receiver.getName())
			.role(memberRole)
			.teamId(teamId)
			.userSpecify(true)
			.expiration(604800L) // 7일
			.expirationDate(getExpiration())
			.senderName(memberName)
			.build();
	}

	private LocalDateTime getExpiration() {
		LocalDateTime now = LocalDateTime.now();
		return now.plusDays(7);
	}
}
