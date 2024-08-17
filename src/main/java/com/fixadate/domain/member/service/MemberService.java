package com.fixadate.domain.member.service;

import static com.fixadate.global.util.RandomUtil.getRandomInt;
import static com.fixadate.global.util.constant.ConstantValue.COMMA;
import static com.fixadate.global.util.constant.ConstantValue.SPACE;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fixadate.domain.member.dto.request.MemberInfoUpdateDto;
import com.fixadate.domain.member.dto.response.MemberInfoDto;
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
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		return null;
	}

	public String generateRandomNickname() {
		final List<String> adjs = List.of(this.adjs.split(COMMA.getValue()));
		final List<String> nouns = List.of(this.nouns.split(COMMA.getValue()));

		return this.getRandomNickname(adjs) + SPACE.getValue() + this.getRandomNickname(nouns);
	}

	public MemberInfoDto getMemberInfo(final String memberId) {
		return memberFacade.getMemberInfo(memberId);
	}

	public MemberInfoDto updateMemberInfo(final MemberInfoUpdateDto memberInfoUpdateDto) {
		return memberFacade.deleteAndGetUploadUrl(memberInfoUpdateDto);
	}

	public String getRandomNickname(final List<String> strings) {
		return strings.get(getRandomInt(strings.size())).trim();
	}
}
