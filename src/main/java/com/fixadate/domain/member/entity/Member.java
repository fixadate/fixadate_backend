package com.fixadate.domain.member.entity;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.googleCalendar.entity.GoogleCredentials;
import com.fixadate.domain.pushKey.entity.PushKey;
import com.fixadate.domain.tag.entity.Tag;
import com.fixadate.global.auth.entity.BaseTimeEntity;
import com.fixadate.global.auth.entity.OAuthProvider;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
@Table(indexes = @Index(name = "oauth_id", columnList = "oauthId", unique = true))
@EntityListeners(IDMakerEntityListener.class)
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

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	private List<Adate> adates;

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	private List<Tag> tags;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "pushKey_id")
	private PushKey pushKey;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "google_credentials_id")
	private GoogleCredentials googleCredentials;

	public void setMemberPushKey(PushKey pushKey) {
		this.pushKey = pushKey;
	}

	public void setGoogleCredentials(GoogleCredentials googleCredentials) {
		this.googleCredentials = googleCredentials;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return AuthorityUtils.commaSeparatedStringToAuthorityList(role);
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() { //memberPrincipal에서 getUsername을 통해 snsId를 얻을 수 있게 함
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
}
