package com.fixadate.domain.invitation.dto.request;


import com.fixadate.domain.invitation.entity.Invitation;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.UUID;

public record InvitationSpecifyRequest(
        @NotBlank String memberName,
        @NotBlank String memberRole,
        @NotBlank Long teamId) {
    public Invitation toEntity() {
        return Invitation.builder()
                .id(UUID.randomUUID().toString())
                .role(memberRole)
                .teamId(teamId)
                .userSpecify(true)
                .expiration(604800L) // 7일
                .expirationDate(getExpiration())
                .memberName(memberName)
                .build();
    }

    private LocalDateTime getExpiration() {
        LocalDateTime now = LocalDateTime.now();
        return now.plusDays(7);
//        return expiration.atZone(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli();
    }
}
