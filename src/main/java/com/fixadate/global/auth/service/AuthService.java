package com.fixadate.global.auth.service;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;

    public Optional<Member> findMemberByOAuthId(String oauthId) {
        return memberRepository.findMemberByOauthId(oauthId);
    }
}
