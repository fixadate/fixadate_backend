package com.fixadate.domain.invitation.dto.response;

import com.fixadate.domain.invitation.entity.Invitation;

public record InvitationResponse(String teamId, String inviterEmail, String inviteeEmail) {

    public static InvitationResponse of(Invitation invitation) {
        return new InvitationResponse(
                invitation.getTeam(),
                invitation.getInviter(),
                invitation.getInvitee()
        );
    }
}
