package com.fixadate.domain.googlecalendar.entity;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.util.converter.EncryptionConverter;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoogleCredentials {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String channelId;

	private String accessToken;

	@Convert(converter = EncryptionConverter.class)
	private String refreshToken;

	private String resourceId;

	private String resourceUri;

	private String resourceState;

	private Long channelExpiration;

	private String channelToken;

	private String userId;

	@OneToOne(mappedBy = "googleCredentials")
	private Member member;

	public void setMember(Member member) {
		this.member = member;
		if (member != null && member.getGoogleCredentials() != this) {
			member.setGoogleCredentials(this);
		}
	}

	public void setGoogleCredentialsOrphan() {
		member.setGoogleCredentials(null);
		this.member = null;
	}
}
