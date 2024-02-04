package com.fixadate.domain.invitation.dto.request;


import com.fixadate.domain.invitation.entity.Invitation;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.UUID;

public record InvitationRequest(String memberRole, Long teamId) {
    public Invitation toEntity() {
        return Invitation.builder()
                .id(UUID.randomUUID().toString())
                .role(memberRole)
                .teamId(teamId)
                .userSpecify(false)
                .expiration(259200L) // 3일
                .expirationDate(getExpiration())
                .memberName(null)
                .build();
    }
    private LocalDateTime getExpiration() {
        LocalDateTime now = LocalDateTime.now();
        return now.plusDays(7);
//        return now.plusDays(7).atZone(atZoneZoneId.of("Asia/Seoul")).toInstant().toEpochMilli();
    }
}
