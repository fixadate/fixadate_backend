package com.fixadate.domain.invitation.dto.response;

public record InvitationMemberList(
    String memberId,
    String name,
    String profileImg,
    String oauthId
) {

}
