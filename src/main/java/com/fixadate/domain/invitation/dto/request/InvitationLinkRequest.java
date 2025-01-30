package com.fixadate.domain.invitation.dto.request;

import com.fixadate.domain.dates.entity.Teams;
import com.fixadate.domain.invitation.entity.Invitation;
import com.fixadate.domain.invitation.entity.InvitationLink;
import com.fixadate.domain.member.entity.Member;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record InvitationLinkRequest(
	@NotBlank int remainCnt,
	@NotNull long teamId) {

	public InvitationLink toEntity(Teams team, Member creator, String inviteCode){
		return InvitationLink.builder()
			.team(team)
			.creator(creator)
			.inviteCode(inviteCode)
			.remainSeat(remainCnt)
			.build();
	}

	private LocalDateTime getExpiration() {
		LocalDateTime now = LocalDateTime.now();
		return now.plusDays(7);
	}
}
