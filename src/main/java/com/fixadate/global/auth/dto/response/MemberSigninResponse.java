package com.fixadate.global.auth.dto.response;

import com.fixadate.domain.member.entity.Member;

public record MemberSigninResponse(
        String id,
        String name,
        String nickname,
        Integer birth,
        String gender,
        String profession,
        String signatureColor,
        String email
) {
    public static MemberSigninResponse of(Member member) {
        return new MemberSigninResponse(
                member.getId(),
                member.getName(),
                member.getNickname(),
                member.getBirth(),
                member.getGender(),
                member.getProfession(),
                member.getSignatureColor(),
                member.getEmail()
        );
    }
}
