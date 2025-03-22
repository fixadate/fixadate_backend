package com.fixadate.domain.invitation.dto.request;

import com.fixadate.domain.dates.entity.Grades;
import com.fixadate.domain.member.entity.Member;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fixadate.domain.invitation.entity.Invitation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InvitationSpecifyRequest(
	@NotNull long teamId,
	List<InvitationEach> each) {

	public record InvitationEach(
		@NotBlank String receiverId,
		@NotBlank String memberName
	){
		public Invitation toEntity(Member sender, Member receiver, long teamId) {
			return Invitation.builder()
				.id(UUID.randomUUID().toString())
				.sender(sender)
				.receiver(receiver)
				.senderName(sender.getName())
				.receiverName(receiver.getName())
				.role(Grades.MANAGER.name())
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
}
