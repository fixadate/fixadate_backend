package com.fixadate.global.auth.service;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.repository.MemberRepository;
import com.fixadate.global.auth.dto.request.MemberRegistRequestDto;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;

    public Optional<Member> findMemberByOAuthId(String oauthId) {
        return memberRepository.findMemberByOauthId(oauthId);
    }

    @Transactional
    public void registMember(MemberRegistRequestDto memberRegistRequestDto) {
        Member member = memberRegistRequestDto.of();
        memberRepository.save(member);
    }
}
