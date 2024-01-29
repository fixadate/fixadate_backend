package com.fixadate.domain.invitation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InvitationRequest {
    private String teamId;
    private String inviteeEmail;
}
