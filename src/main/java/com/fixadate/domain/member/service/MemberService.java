package com.fixadate.domain.member.service;

import com.fixadate.domain.member.dto.request.AdateColorNameRequestDto;
import com.fixadate.domain.member.dto.response.MemberColorResponse;
import com.fixadate.domain.member.entity.AdateColorTypes;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.exception.AdateColorTypeNameDuplicatedException;
import com.fixadate.domain.member.exception.MemberNotFoundException;
import com.fixadate.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;

import java.util.*;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final Random random = new Random(System.currentTimeMillis());


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }

    @Transactional
    public void saveAdateColorAndName(AdateColorNameRequestDto adateColorNameRequestDto, Member member) {
        AdateColorTypes adateColorTypes = member.getAdateColorTypes();
        validateAdateColorNameUniqueness(adateColorNameRequestDto.name(), member);
        adateColorTypes.addColor(adateColorNameRequestDto.name(), adateColorNameRequestDto.color());
        memberRepository.save(member);
    }

    private void validateAdateColorNameUniqueness(String name, Member member) {
        AdateColorTypes adateColorTypes = member.getAdateColorTypes();
        if (adateColorTypes.ifContainsKey(name)) {
            throw new AdateColorTypeNameDuplicatedException();
        }
    }

    @Transactional
    public Member getMemberWithAdateColorTypes(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                MemberNotFoundException::new);
        member.getAdateColorTypes().getKeyAndValue();
        return member;
    }

    public String getRandomNickname(List<String> strings) {
        return strings.get(random.nextInt(strings.size())).trim();
    }

    public List<MemberColorResponse> getMemberColor(Long memberId) {
        Member member = getMemberWithAdateColorTypes(memberId);
        AdateColorTypes adateColorTypes = member.getAdateColorTypes();
        List<MemberColorResponse> memberColorResponses = adateColorTypes.toMemberColorResponses();

        return memberColorResponses;
    }
}
