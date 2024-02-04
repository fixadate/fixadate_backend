package com.fixadate.domain.invitation.dto.response;

import com.fixadate.domain.invitation.entity.Invitation;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public record InvitationResponse(Long teamId, LocalDateTime dateTime, String memberName, String memberRole) {

    public static InvitationResponse of(Invitation invitation) {
        return new InvitationResponse(invitation.getTeamId(),
                getLocalDateTime(invitation.getExpiration()), invitation.getMemberName(), invitation.getRole());
    }

    private static LocalDateTime getLocalDateTime(Long milliseconds) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), ZoneId.of("Asia/Seoul"));
    }
}
