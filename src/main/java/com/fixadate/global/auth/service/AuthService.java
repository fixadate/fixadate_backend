package com.fixadate.global.auth.service;


import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.repository.MemberRepository;
import com.fixadate.global.auth.dto.request.MemberRegistRequest;
import com.fixadate.global.auth.exception.MemberSigninException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
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
    public void registMember(MemberRegistRequest memberRegistRequest) {
        //fixme modelmapper 사용해서 구현해보기
        Member member = memberRegistRequest.of();
        memberRepository.save(member);
    }

    public ResponseCookie createHttpOnlyCooke(String token) {
        return ResponseCookie.from(REFRESH_TOKEN, token)
                .secure(true)
                .httpOnly(true)
                .build();
    }
}
