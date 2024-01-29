package com.fixadate.domain.invitation.dto.response;

import com.fixadate.domain.invitation.entity.Invitation;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public record InvitationResponse(String invitationId, Long teamId, LocalDateTime dateTime) {

    public static InvitationResponse of(Invitation invitation) {
        return new InvitationResponse(invitation.getId(), invitation.getTeamId(), getLocalDateTime(invitation.getExpiration()));
    }

    private static LocalDateTime getLocalDateTime(Long milliseconds) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), ZoneId.of("Asia/Seoul"));
    }
}
