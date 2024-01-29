package com.fixadate.domain.invitation.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import java.util.concurrent.TimeUnit;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "invitation")
public class Invitation {
    //fixme : 초대수단(이메일 or 전화번호 or else)에 따라서 주석 수정하기
    //초대하는 사람의 이메일
    private String inviter;

    //초대하려고 하는 팀
    @Id
    private String team;

    //초대받는 사람의 이메일
    private String invitee;

    @TimeToLive(unit = TimeUnit.MILLISECONDS)
    private Long expiration;
}
