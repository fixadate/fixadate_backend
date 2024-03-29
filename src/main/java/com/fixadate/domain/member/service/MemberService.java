package com.fixadate.domain.member.service;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.exception.MemberNotFoundException;
import com.fixadate.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final Random random = new Random(System.currentTimeMillis());

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
    public String getRandomNickname(List<String> strings) {
        return strings.get(random.nextInt(strings.size())).trim();
    }

    public Member getMemberFromId(Long memberId) {
        return memberRepository.findMemberById(memberId)
                .orElseThrow(MemberNotFoundException::new);
    }
}
