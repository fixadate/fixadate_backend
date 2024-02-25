package com.fixadate.domain.member.entity;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.colortype.entity.ColorType;
import com.fixadate.global.auth.entity.BaseTimeEntity;
import com.fixadate.global.oauth.entity.OAuthProvider;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Table(indexes = @Index(name = "oauth_id", columnList = "oauthId", unique = true))
public class Member extends BaseTimeEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String oauthId;
    @Enumerated(EnumType.STRING)
    private OAuthProvider oauthPlatform;
    private String name;
    private String profileImg;
    private String nickname;
    private Integer birth;
    private String gender;
    private String profession;
    private String signatureColor;
    // 로그인 정보 식별 값, 프로필 사진, 필명, 이름, 성별, 생년월일, 이메일

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Adate> adates;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<ColorType> colorTypes;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        return authorities;
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
