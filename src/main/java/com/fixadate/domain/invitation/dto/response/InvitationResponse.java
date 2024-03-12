package com.fixadate.domain.invitation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fixadate.domain.invitation.entity.Invitation;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public record InvitationResponse(
        long teamId,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd / HH:mm", timezone = "Asia/Seoul")
        LocalDateTime dateTime,
        String memberName,
        String memberRole) {

    public static InvitationResponse of(Invitation invitation) {
        return new InvitationResponse(invitation.getTeamId(),
                invitation.getExpirationDate(), invitation.getMemberName(), invitation.getRole());
    }
}
