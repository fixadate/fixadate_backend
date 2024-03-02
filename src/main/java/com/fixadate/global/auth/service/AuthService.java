package com.fixadate.global.auth.service;


import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.repository.MemberRepository;
import com.fixadate.global.auth.dto.request.MemberRegistRequestDto;

import com.fixadate.global.auth.exception.MemberSigninException;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.fixadate.global.oauth.ConstantValue.REFRESH_TOKEN;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;

    public Member findMemberByOAuthId(String oauthId) {
        return memberRepository.findMemberByOauthId(oauthId).orElseThrow(MemberSigninException::new);
    }

    @Transactional
    public void registMember(MemberRegistRequestDto memberRegistRequestDto) {
        Member member = memberRegistRequestDto.of();
        memberRepository.save(member);
    }

    public Cookie createHttpOnlyCooke(String token) {
        Cookie cookie = new Cookie(REFRESH_TOKEN, token);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        return cookie;
    }
}
