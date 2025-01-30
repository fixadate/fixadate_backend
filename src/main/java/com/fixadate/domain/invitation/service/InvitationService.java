package com.fixadate.domain.invitation.service;

import static com.fixadate.global.exception.ExceptionCode.NOT_FOUND_MEMBER_ID;

import com.fixadate.domain.dates.entity.Teams;
import com.fixadate.domain.dates.repository.TeamRepository;
import com.fixadate.domain.invitation.entity.InvitationLink;
import com.fixadate.domain.invitation.repository.InvitationLinkRepository;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.service.repository.MemberRepository;
import com.fixadate.global.exception.notfound.MemberNotFoundException;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fixadate.domain.invitation.dto.request.InvitationLinkRequest;
import com.fixadate.domain.invitation.dto.request.InvitationSpecifyRequest;
import com.fixadate.domain.invitation.dto.response.InvitationResponse;
import com.fixadate.domain.invitation.entity.Invitation;
import com.fixadate.domain.invitation.exception.InvitationNotFountException;
import com.fixadate.domain.invitation.repository.InvitationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvitationService {
	private final InvitationRepository invitationRepository;
	private final InvitationLinkRepository invitationLinkRepository;
	private final MemberRepository memberRepository;
	private final TeamRepository teamRepository;
	private final ApplicationEventPublisher eventPublisher;

	@Transactional
	public String registInvitation(Member member, InvitationLinkRequest invitationLinkRequest) {
		// todo: 팀 초대 권한 검증 로직
		// todo: 팀 초대 인원 가능여부 확인 로직

		String inviteCode = "";
		Teams foundTeam = teamRepository.findById(invitationLinkRequest.teamId()).orElseThrow(
			() -> new RuntimeException("")
		);

		InvitationLink invitationLink = invitationLinkRequest.toEntity(foundTeam, member, inviteCode);
		InvitationLink createLink = invitationLinkRepository.save(invitationLink);
		return createLink.getInviteCode();
	}

	public InvitationResponse getInvitationFromTeamId(Long teamId) {
		Invitation invitation = invitationRepository.findByTeamId(teamId)
			.orElseThrow(InvitationNotFountException::new);
		return InvitationResponse.of(invitation);
	}

	public InvitationResponse getInvitationById(String invitationId) {
		Invitation invitation = invitationRepository.findById(invitationId)
			.orElseThrow(InvitationNotFountException::new);
		return InvitationResponse.of(invitation);
	}

	public boolean inviteSpecifyMember(Member member, InvitationSpecifyRequest invitationSpecifyRequest) {

		// todo: 팀 초대 권한 검증 로직
		// todo: 팀 초대 인원 가능여부 확인 로직

		Teams foundTeam = teamRepository.findById(invitationSpecifyRequest.teamId()).orElseThrow(
			() -> new RuntimeException("")
		);

		Member receiver = memberRepository.findMemberById(invitationSpecifyRequest.receiverId()).orElseThrow(
			() -> new MemberNotFoundException(NOT_FOUND_MEMBER_ID)
		);
		Invitation invitation = invitationSpecifyRequest.toEntity(member, receiver);
		Invitation createdInvitation = invitationRepository.save(invitation);

		// todo: push alarm

		return invitationRepository.existsById(createdInvitation.getId());
	}

	public List<InvitationResponse> getInvitationResponseFromTeamId(Long teamId) {
		List<Invitation> invitations = invitationRepository.findAllByTeamId(teamId);
		return getResponseFromInvitation(invitations);
	}

	private List<InvitationResponse> getResponseFromInvitation(List<Invitation> invitations) {
		return invitations.stream()
			.map(InvitationResponse::of)
			.toList();
	}
}
