package com.fixadate.domain.member.service;

import java.util.List;
import java.util.Random;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fixadate.domain.member.dto.response.MemberInfoResponse;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.mapper.MemberMapper;
import com.fixadate.domain.member.repository.MemberRepository;
import com.fixadate.global.exception.ExceptionCode;
import com.fixadate.global.exception.notFound.MemberNotFoundException;
import com.fixadate.global.util.S3Util;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
	private final MemberRepository memberRepository;
	private final S3Util s3Util;
	private final Random random = new Random(System.currentTimeMillis());

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return null;
	}

	public MemberInfoResponse getMemberInfo(String memberId) {
		Member member = memberRepository.findMemberById(memberId).orElseThrow(() -> new MemberNotFoundException(
			ExceptionCode.NOT_FOUND_MEMBER_ID));
		return MemberMapper.toInfoResponse(member, s3Util.generatePresignedUrlForDownload(member.getProfileImg()));
	}

	public String getRandomNickname(List<String> strings) {
		return strings.get(random.nextInt(strings.size())).trim();
	}
}
