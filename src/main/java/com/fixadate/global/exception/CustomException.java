package com.fixadate.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CustomException {
    //Native Authentication Error
    NativeAuthenticationError001("0N0A0E0001","Native Authentication Error. 네이티브 인증 정보가 일치하지 않음"),

    //Team Member Role Error
    TeamMemberRoleError001("0T0M0R0E01", "Team Member Role Error. Team Member의 Role이 ADMIN, MEMBER, OWNER 중에서 설정되지 않음"),

    //Team Member Info Error
    TeamMemberInfoError001("0T0M0I0E01", "Team Member Info Error. Team Member의 Join을 위해 Team Id, Member Id를 검색하였으나 조회되지 않음");
    private final String customCode;
    private final String errorMsg;
}
