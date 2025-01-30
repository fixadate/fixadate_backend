package com.fixadate.domain.invitation.controller;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.jwt.MemberPrincipal;
import org.springframework.http.ResponseEntity;

import com.fixadate.domain.invitation.dto.request.InvitationLinkRequest;
import com.fixadate.domain.invitation.dto.request.InvitationSpecifyRequest;
import com.fixadate.domain.invitation.dto.response.InvitationResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "InvitationController", description = "InvitationController 입니다.")
public interface InvitationController {

	@Operation(summary = "초대 링크 생성", description = "링크를 생성합니다.", deprecated = true)
	ResponseEntity<String> registInvitation(
		@AuthenticationPrincipal final MemberPrincipal memberPrincipal,
		@Valid InvitationLinkRequest invitationLinkRequest);

	@Operation(summary = "멤버 초대", description = "특정 멤버를 팀에 초대합니다.", deprecated = true)
	ResponseEntity<?> inviteMemberToTeams(
		@AuthenticationPrincipal final MemberPrincipal memberPrincipal,
		@Valid InvitationSpecifyRequest requestDto);

	@Operation(summary = "초대 조회", description = "초대 코드를 입력하여 초대에 응답합니다.", deprecated = true)
	ResponseEntity<?> checkInvitationById(String id);

	@Operation(summary = "초대 존재 여부 확인", description = "중복 방지를 위해 이미 생성된 초대장이 있는지 조회합니다.", deprecated = true)
	ResponseEntity<InvitationResponse> isInvitationExist(Long datesId);

	@Operation(summary = "팀별 초대 조회", description = "특정 팀에 대한 초대를 조회합니다.", deprecated = true)
	ResponseEntity<?> getSpecifyInvitationByTeamId(Long teamId);

	@Operation(summary = "초대 코드 검증", description = "특정 팀에 대한 초대링크를 수락합니다.", deprecated = true)
	public ResponseEntity<Boolean> validateInvitationLink(
		@AuthenticationPrincipal final MemberPrincipal memberPrincipal,
		@PathVariable String inviteCode);

	@Operation(summary = "초대 코드 비활성화", description = "초대링크를 비활성화합니다.", deprecated = true)
	public ResponseEntity<Boolean> deactivateInvitationLink(
		@AuthenticationPrincipal final MemberPrincipal memberPrincipal,
		@PathVariable String inviteCode);
}
