package com.fixadate.domain.member.service;

import java.util.List;
import java.util.Random;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fixadate.domain.member.dto.request.MemberInfoUpdateRequest;
import com.fixadate.domain.member.dto.response.MemberInfoResponse;
import com.fixadate.global.facade.MemberFacade;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
	private final Random random = new Random(System.currentTimeMillis());
	private final MemberFacade memberFacade;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return null;
	}

	public MemberInfoResponse getMemberInfo(String memberId) {
		return memberFacade.getMemberInfo(memberId);
	}

	public MemberInfoResponse updateMemberInfo(String memberId, MemberInfoUpdateRequest memberInfoUpdateRequest) {
		return memberFacade.deleteAndGetUploadUrl(memberId, memberInfoUpdateRequest);
	}

	public String getRandomNickname(List<String> strings) {
		return strings.get(random.nextInt(strings.size())).trim();
	}
}
