package com.fixadate.domain.invitation.entity;

import com.fixadate.domain.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "invitation_histories")
public class InvitationHistory {
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
	private boolean userSpecify; // 직접 초대 혹은 링크 초대인지 여부
	@Column(nullable = false)
	private String role;
}
