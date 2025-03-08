package com.fixadate.domain.invitation.service;

import static com.fixadate.global.exception.ExceptionCode.NOT_FOUND_MEMBER_ID;

import com.fixadate.domain.auth.entity.BaseEntity.DataStatus;
import com.fixadate.domain.dates.entity.TeamMembers;
import com.fixadate.domain.dates.entity.Teams;
import com.fixadate.domain.dates.repository.TeamMembersRepository;
import com.fixadate.domain.dates.repository.TeamRepository;
import com.fixadate.domain.invitation.dto.response.InvitableMemberListResponse;
import com.fixadate.domain.invitation.entity.Invitation.InviteStatus;
import com.fixadate.domain.invitation.entity.InvitationHistory;
import com.fixadate.domain.invitation.entity.InvitationLink;
import com.fixadate.domain.invitation.repository.InvitationHistoryRepository;
import com.fixadate.domain.invitation.repository.InvitationLinkRepository;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.service.repository.MemberRepository;
import com.fixadate.global.exception.notfound.MemberNotFoundException;
import java.util.ArrayList;
import java.util.List;

import java.util.Optional;
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
	private final TeamMembersRepository teamMembersRepository;
	private final InvitationHistoryRepository invitationHistoryRepository;
	private final ApplicationEventPublisher eventPublisher;

	@Transactional
	public String registInvitation(Member member, InvitationLinkRequest invitationLinkRequest) {
		// todo: 팀 초대 권한 검증 로직
		// todo: 팀 초대 인원 가능여부 확인 로직

		Teams foundTeam = teamRepository.findById(invitationLinkRequest.teamId()).orElseThrow(
			() -> new RuntimeException("")
		);

		InvitationLink invitationLink = invitationLinkRequest.toEntity(foundTeam, member);
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

	public boolean validateInvitationLink(Member receiver, String inviteCode) {
		// todo: 팀에 초대가능한 사람인지 확인
		Optional<InvitationLink> invitationLinkOptional = invitationLinkRepository.findByInviteCode(inviteCode)
			.filter(link -> link.isActive() && !link.isExpired());
		if(invitationLinkOptional.isEmpty()){
			return false;
		}

		InvitationLink invitationLink = invitationLinkOptional.get();
		invitationLink.decreaseRemainCnt();

		createInvitationHistory(invitationLink.getTeam().getId(), invitationLink.getCreator(), receiver, false, "MEMBER");

		// todo: push alarm
		return true;
	}

	public boolean deactivateInvitationLink(Member member, String inviteCode) {
		// todo: 비활성화 가능한 사람인지 확인
		Optional<InvitationLink> invitationLinkOptional = invitationLinkRepository.findByInviteCode(inviteCode);
		if(invitationLinkOptional.isEmpty()){
			return false;
		}
		if(invitationLinkOptional.get().isExpired()){
			return false;
		}
		if(!invitationLinkOptional.get().isActive()){
			return false;
		}
		InvitationLink invitationLink = invitationLinkOptional.get();
		invitationLink.deactivate();

		return !invitationLink.isActive();
	}

	public boolean acceptInvitation(Member member, String id) {
		Optional<Invitation> invitationOptional = invitationRepository.findById(id);
		if(invitationOptional.isEmpty()){
			return false;
		}
		Invitation invitation = invitationOptional.get();
		if(!member.getId().equals(invitation.getReceiver().getId())){
			throw new RuntimeException("invalid receiver");
		}
		invitation.accept();

		createInvitationHistory(invitation.getTeamId(), invitation.getSender(), invitation.getReceiver(), true, invitation.getRole());

		return InviteStatus.ACCEPTED.equals(invitation.getStatus());
	}

	public boolean declineInvitation(Member member, String id) {
		Optional<Invitation> invitationOptional = invitationRepository.findById(id);
		if(invitationOptional.isEmpty()){
			return false;
		}
		Invitation invitation = invitationOptional.get();
		if(!member.getId().equals(invitation.getReceiver().getId())){
			throw new RuntimeException("invalid receiver");
		}
		invitation.decline();
		return InviteStatus.DECLINED.equals(invitation.getStatus());
	}

	public void createInvitationHistory(Long teamId, Member sender, Member receiver, boolean userSpecify, String role){
		InvitationHistory invitationHistory = InvitationHistory.builder()
			.teamId(teamId)
			.sender(sender)
			.receiver(receiver)
			.userSpecify(userSpecify)
			.role(role)
			.build();
		invitationHistoryRepository.save(invitationHistory);
	}

	public List<InvitableMemberListResponse> getInvitableTeamMemberList(Member teamCreator, Long teamId, String email) {

		// 팀 초대 가능한 사람인지 확인
		TeamMembers teamMembers = teamMembersRepository.findByTeam_IdAndMember_Id(teamId, teamCreator.getId())
			.orElseThrow(() -> new RuntimeException(""));
		if(!(teamMembers.isOwner() || teamMembers.isManager())){
			throw new RuntimeException("");
		}
		List<TeamMembers> teamMembersList = teamMembersRepository.findAllByTeamAndStatusIs(teamMembers.getTeam(), DataStatus.ACTIVE);
		List<String> memberIds = teamMembersList.stream()
			.map(TeamMembers::getMember)
			.map(Member::getId)
			.toList();

		// 초대가능한 사람 목록 불러오기
		// 팀 member가 아닌 사람으로 filter위
		List<Member> invitableMembers = memberRepository.findMembersByEmailContainingAndExcludingIds(email, memberIds);

		return invitableMembers.stream().map(InvitableMemberListResponse::of).toList();
	}
}
