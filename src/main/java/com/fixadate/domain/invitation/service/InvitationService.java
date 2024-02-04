package com.fixadate.domain.invitation.service;

import com.fixadate.domain.invitation.dto.request.InvitationRequestDto;
import com.fixadate.domain.invitation.dto.request.InvitationSpecifyRequestDto;
import com.fixadate.domain.invitation.dto.response.InvitationResponse;
import com.fixadate.domain.invitation.entity.Invitation;
import com.fixadate.domain.invitation.exception.InvitationNotFountException;
import com.fixadate.domain.invitation.repository.InvitationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvitationService {
    private final InvitationRepository invitationRepository;

    public void registInvitation(InvitationRequestDto invitationRequestDto) {
        Invitation invitation = invitationRequestDto.toEntity();
        invitationRepository.save(invitation);
    }

    public InvitationResponse getInvitationFromTeamId(Long teamId) {
        Invitation invitation = invitationRepository.findByTeamId(teamId)
                .orElseThrow(InvitationNotFountException::new);
        return InvitationResponse.of(invitation);
    }

    public InvitationResponse getInvitationById(String invitationId) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(InvitationNotFountException::new);
        log.info(invitation.getExpiration().toString());
        return InvitationResponse.of(invitation);
    }

    public void  inviteSpecifyMember(InvitationSpecifyRequestDto invitationSpecifyRequestDto) {
        Invitation invitation = invitationSpecifyRequestDto.toEntity();
        invitationRepository.save(invitation);
    }
}
