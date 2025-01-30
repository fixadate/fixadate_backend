package com.fixadate.domain.invitation.entity;

import com.fixadate.domain.auth.entity.BaseTimeEntity;
import com.fixadate.domain.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Getter
@Builder
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "invitation_histories")
public class InvitationHistory extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
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
