package com.fixadate.domain.invitation.service;

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

    public String registInvitation(Long teamId) {
        Invitation invitation = createInvitation(teamId);
        invitationRepository.save(invitation);
        return invitation.getId();
    }

    private Invitation createInvitation(Long teamId) {
        return Invitation.builder()
                .id(UUID.randomUUID().toString())
                .teamId(teamId)
                .expiration(getExpiration())
                .build();
    }

    private Long getExpiration() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiration = now.plusDays(7).with(LocalTime.of(23, 59, 59));
        return expiration.atZone(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli();
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
}
