package com.fixadate.global.jwt;

import java.io.Serializable;

import org.springframework.security.core.userdetails.User;

import com.fixadate.domain.member.entity.Member;

import lombok.Getter;

@Getter
public class MemberPrincipal extends User implements Serializable {
	private final Member member;

	public MemberPrincipal(Member member) {
		super(member.getId(), member.getUsername(),
			  member.getAuthorities()
		);
		this.member = member;
	}

	public String getMemberId() {
		return this.member.getId();
	}
}
