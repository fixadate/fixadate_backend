package com.fixadate.domain.member.entity;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fixadate.domain.auth.entity.BaseTimeEntity;
import com.fixadate.domain.auth.entity.OAuthProvider;
import com.fixadate.domain.googlecalendar.entity.GoogleCredentials;
import com.fixadate.domain.pushkey.entity.PushKey;

import IDMaker.idmaker.IDMaker;
import IDMaker.idmaker.IDMakerEntityListener;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Table(
	indexes = @Index(name = "oauth_id", columnList = "oauthId", unique = true),
	uniqueConstraints = @UniqueConstraint(name = "member_identifier", columnNames = {"name", "email", "oauthPlatform"})
)
@EntityListeners(IDMakerEntityListener.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Member extends BaseTimeEntity implements UserDetails {

	@Id
	@IDMaker(length = 6)
	private String id;

	@Column(nullable = false)
	private String oauthId;

	@Enumerated(EnumType.STRING)
	private OAuthProvider oauthPlatform;

	@Column(nullable = false)
	private String name;

	private String profileImg;

	@Column(nullable = false)
	private String nickname;

	private String birth;

	private String gender;

	private String profession;

	@Column(nullable = false)
	private String signatureColor;

	@Column(nullable = false)
	private String email;

	private String role;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinColumn(name = "pushKey_id")
	private PushKey pushKey;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "google_credentials_id")
	private GoogleCredentials googleCredentials;

	@Override
	@JsonIgnore
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return AuthorityUtils.commaSeparatedStringToAuthorityList(role);
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return name;
	}

	@Override
	public boolean isAccountNonExpired() {
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return false;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

	public void updateMemberPushKey(final PushKey pushKey) {
		this.pushKey = pushKey;
	}

	public void updateGoogleCredentials(final GoogleCredentials googleCredentials) {
		this.googleCredentials = googleCredentials;
	}

	public void updateNickname(final String nickname) {
		this.nickname = nickname;
	}

	public void updateProfileImg(final String profileImg) {
		this.profileImg = profileImg;
	}

	public void updateSignatureColor(final String signatureColor) {
		this.signatureColor = signatureColor;
	}

	public void updateProfession(final String profession) {
		this.profession = profession;
	}
}
