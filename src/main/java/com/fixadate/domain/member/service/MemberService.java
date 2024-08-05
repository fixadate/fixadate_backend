package com.fixadate.domain.member.service;

import static com.fixadate.global.util.RandomUtil.current;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
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

	private final MemberFacade memberFacade;

	@Value("${randNick.adjs}")
	private String adjs;

	@Value("${randNick.nouns}")
	private String nouns;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return null;
	}

	public String generateRandomNickname() {
		List<String> adjs = List.of(this.adjs.split(","));
		List<String> nouns = List.of(this.nouns.split(","));

		return getRandomNickname(adjs) + " " + getRandomNickname(nouns);
	}

	public MemberInfoResponse getMemberInfo(String memberId) {
		return memberFacade.getMemberInfo(memberId);
	}

	public MemberInfoResponse updateMemberInfo(String memberId, MemberInfoUpdateRequest memberInfoUpdateRequest) {
		return memberFacade.deleteAndGetUploadUrl(memberId, memberInfoUpdateRequest);
	}

	public String getRandomNickname(List<String> strings) {
		return strings.get(current().nextInt(strings.size())).trim();
	}
}
