package com.fixadate.domain.invitation.dto.request;


import com.fixadate.domain.invitation.entity.Invitation;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.UUID;

public record InvitationSpecifyRequestDto(String memberName, String memberRole, Long teamId) {
    public Invitation toEntity() {
        return Invitation.builder()
                .id(UUID.randomUUID().toString())
                .role(memberRole)
                .teamId(teamId)
                .isUserSpecify(true)
                .expiration(getExpiration())
                .memberName(memberName)
                .build();
    }

    private Long getExpiration() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiration = now.plusDays(7).with(LocalTime.of(23, 59, 59));
        return expiration.atZone(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli();
    }
}
