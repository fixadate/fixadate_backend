package com.fixadate.global.jwt;

import com.fixadate.domain.member.entity.Member;
import lombok.Getter;
import org.springframework.security.core.userdetails.User;

import java.io.Serializable;
@Getter
public class MemberPrincipal extends User implements Serializable {
    private final Member member;

    public MemberPrincipal(Member member) {
        super(member.getId(), member.getUsername(),
                member.getAuthorities());
        this.member = member;
    }

    public String getMemberId() {
        return member.getId();
    }
}
