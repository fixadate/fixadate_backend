package com.fixadate.domain.member.service;

import com.fixadate.domain.member.dto.request.AdateColorNameRequestDto;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.exception.UnknownMemberException;
import com.fixadate.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }

    public void saveMember(Member member) {
        memberRepository.save(member);
    }

    public Optional<Member> findMemberByOAuthId(String oAuthId) {
        return memberRepository.findMemberByOauthId(oAuthId);
    }
    @Transactional
    public void saveAdateColorAndName(AdateColorNameRequestDto adateColorNameRequestDto, Member member) {
        Map<String, String> adateColorTypes = member.getAdateColorTypes();
        adateColorTypes.put(adateColorNameRequestDto.getColor(), adateColorNameRequestDto.getName());
        memberRepository.save(member);
    }
    @Transactional
    public Member getMemberWithAdateColorTypes(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new UnknownMemberException());
        member.getAdateColorTypes().forEach((key, value) -> {});
        return member;
    }
}
