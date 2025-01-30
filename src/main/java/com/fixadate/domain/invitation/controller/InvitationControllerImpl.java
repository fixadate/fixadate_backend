package com.fixadate.domain.invitation.controller;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.jwt.MemberPrincipal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.fixadate.domain.invitation.dto.request.InvitationLinkRequest;
import com.fixadate.domain.invitation.dto.request.InvitationSpecifyRequest;
import com.fixadate.domain.invitation.dto.response.InvitationResponse;
import com.fixadate.domain.invitation.service.InvitationService;
import com.fixadate.global.annotation.RestControllerWithMapping;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestControllerWithMapping("/v1/invitation")
public class InvitationControllerImpl implements InvitationController {
	private final InvitationService invitationService;

	@Override
	@PostMapping()
	public ResponseEntity<String> registInvitation(
		@AuthenticationPrincipal final MemberPrincipal memberPrincipal,
		@Valid @RequestBody InvitationLinkRequest invitationLinkRequest) {
		final Member member = memberPrincipal.getMember();
		String invitationId = invitationService.registInvitation(member, invitationLinkRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(invitationId);
	}

	@Override
	@GetMapping() // 초대 코드를 입력해서 초대에 응답하는 로직
	public ResponseEntity<?> checkInvitationById(@RequestParam String id) {
		return ResponseEntity.ok(invitationService.getInvitationById(id));
	}

	//중복 방지를 위해 이미 생성된 초대장이 있는지 조회하는 로직
	@Override
	@GetMapping("/check")
	public ResponseEntity<InvitationResponse> isInvitationExist(@RequestParam Long datesId) {
		return ResponseEntity.ok(invitationService.getInvitationFromTeamId(datesId));
	}

	@Override
	@PostMapping("/specify")
	public ResponseEntity<?> inviteMemberToTeams(
		@AuthenticationPrincipal final MemberPrincipal memberPrincipal,
		@Valid @RequestBody InvitationSpecifyRequest requestDto) {
		final Member member = memberPrincipal.getMember();
		boolean result = invitationService.inviteSpecifyMember(member, requestDto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@Override
	@GetMapping("/specify")
	public ResponseEntity<?> getSpecifyInvitationByTeamId(@RequestParam Long teamId) {
		List<InvitationResponse> responses = invitationService.getInvitationResponseFromTeamId(teamId);
		return ResponseEntity.ok(responses);
	}
}
