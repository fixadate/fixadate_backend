package com.fixadate.domain.invitation.entity;

import com.fixadate.domain.member.entity.Member;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "invitation")
public class Invitation {
	@Id
	private String id;
	@Indexed
	private Long teamId;
	@ManyToOne(fetch = FetchType.LAZY) // 보낸 사람 (팀원 or 팀장)
	@JoinColumn(name = "sender_id", nullable = false)
	private Member sender;

	@ManyToOne(fetch = FetchType.LAZY) // 받은 사람 (초대 대상자)
	@JoinColumn(name = "receiver_id", nullable = false)
	private Member receiver;
	private boolean userSpecify;
	@Column(nullable = false)
	private String role;
	private String senderName;
	private String receiverName;
	@TimeToLive
	private Long expiration;
	private LocalDateTime expirationDate;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private InviteStatus status = InviteStatus.PENDING;

	public enum InviteStatus {
		PENDING, ACCEPTED, DECLINED
	}
	public void accept(){
		if(!InviteStatus.PENDING.equals(this.status)){
			throw new RuntimeException("invalid status");
		}
		this.status = InviteStatus.ACCEPTED;
	}

	public void decline(){
		if(!InviteStatus.PENDING.equals(this.status)){
			throw new RuntimeException("invalid status");
		}
		this.status = InviteStatus.DECLINED;
	}
}
