package com.fixadate.domain.member.entity;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.colortype.entity.ColorType;
import com.fixadate.global.auth.entity.BaseTimeEntity;
import com.fixadate.global.oauth.entity.OAuthProvider;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Adate> adates;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
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
