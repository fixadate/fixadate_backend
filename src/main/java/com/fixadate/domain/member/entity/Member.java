package com.fixadate.domain.member.entity;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fixadate.domain.auth.entity.BaseEntity;
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
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(
	indexes = @Index(name = "oauth_id", columnList = "oauthId", unique = true),
	uniqueConstraints = @UniqueConstraint(name = "member_identifier", columnNames = {"name", "email", "oauthPlatform"})
)
@EntityListeners(IDMakerEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(exclude = {"pushKey", "googleCredentials"})
@Getter
public class Member extends BaseEntity implements UserDetails {

	@Id
	@IDMaker(length = 6)
	@Column(name = "member_id")
	private String id;

	@Column(nullable = false, unique = true)
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

	private String role; // 추후 enum으로 변경

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinColumn(name = "pushKey_id")
	private PushKey pushKey;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "google_credentials_id")
	private GoogleCredentials googleCredentials;

	@OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	private MemberPlans memberPlan;

	@Column
	private String storyBoard;

	@Builder
	private Member(
		final String oauthId,
		final OAuthProvider oauthPlatform,
		final String name,
		final String profileImg,
		final String nickname,
		final String birth,
		final String gender,
		final String profession,
		final String signatureColor,
		final String email,
		final String role,
		final PushKey pushKey,
		final GoogleCredentials googleCredentials
	) {
		this.oauthId = oauthId;
		this.oauthPlatform = oauthPlatform;
		this.name = name;
		this.profileImg = profileImg;
		this.nickname = nickname;
		this.birth = birth;
		this.gender = gender;
		this.profession = profession;
		this.signatureColor = signatureColor;
		this.email = email;
		this.role = role;
		this.pushKey = pushKey;
		this.googleCredentials = googleCredentials;
	}

	@Override
	@JsonIgnore
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return AuthorityUtils.commaSeparatedStringToAuthorityList(role);
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

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return this.name;
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

	public void updateStoryBoard(final String storyBoard) {
		if(storyBoard != null && storyBoard.length() > 30) {
			throw new IllegalArgumentException("storyBoard length must be less than 30");
		}
		this.storyBoard = storyBoard;
	}
}
