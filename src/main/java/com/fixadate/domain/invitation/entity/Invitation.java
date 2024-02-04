package com.fixadate.domain.invitation.entity;

import com.fixadate.domain.member.entity.Member;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "invitation")
public class Invitation {
    @Id // random_invitaion_id
    private String id;
    @Indexed
    private Long teamId;

    private boolean userSpecify;

    @Column(nullable = false)
    private String role;

    private String memberName;

    @TimeToLive(unit = TimeUnit.MILLISECONDS)
    private Long expiration;
}
