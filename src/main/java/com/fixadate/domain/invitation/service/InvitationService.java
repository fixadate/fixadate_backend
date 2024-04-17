package com.fixadate.domain.invitation.service;

import com.fixadate.domain.invitation.dto.request.InvitationRequest;
import com.fixadate.domain.invitation.dto.request.InvitationSpecifyRequest;
import com.fixadate.domain.invitation.dto.response.InvitationResponse;
import com.fixadate.domain.invitation.entity.Invitation;
import com.fixadate.domain.invitation.exception.InvitationNotFountException;
import com.fixadate.domain.invitation.repository.InvitationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvitationService {
    private final InvitationRepository invitationRepository;

    @Transactional
    public String registInvitation(InvitationRequest invitationRequest) {
        Invitation invitation = invitationRequest.toEntity();
        invitationRepository.save(invitation);
        return invitation.getId();
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

    public void  inviteSpecifyMember(InvitationSpecifyRequest invitationSpecifyRequest) {
        Invitation invitation = invitationSpecifyRequest.toEntity();
        invitationRepository.save(invitation);
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
