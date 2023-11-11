package com.fixadate.global.jwt;

import com.fixadate.domain.member.entity.Member;
import java.io.Serializable;
import lombok.Getter;
import org.springframework.security.core.userdetails.User;
@Getter
public class MemberPrincipal extends User implements Serializable {
    private final Member member;

    public MemberPrincipal(Member member) {
        super(member.getOauthId(), member.getPassword(),
                member.getAuthorities());
        this.member = member;
    }
}
