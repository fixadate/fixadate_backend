package com.fixadate.domain.member.entity;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.colortype.entity.ColorType;
import com.fixadate.domain.googleCalendar.entity.GoogleCredentials;
import com.fixadate.domain.pushKey.entity.PushKey;
import com.fixadate.global.auth.entity.BaseTimeEntity;
import com.fixadate.global.oauth.entity.OAuthProvider;
import com.fixadate.global.util.RandomValueUtil;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Table(indexes = @Index(name = "oauth_id", columnList = "oauthId", unique = true))
public class Member extends BaseTimeEntity implements UserDetails {

    @Id
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

    private Integer birth;

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
    private List<ColorType> colorTypes;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "pushKey_id")
    private PushKey pushKey;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
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

    public void createMemberId() {
        this.id = System.currentTimeMillis() + RandomValueUtil.createRandomString(7);
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
