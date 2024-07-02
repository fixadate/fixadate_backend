package com.fixadate.domain.member.service;

import java.util.List;
import java.util.Random;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fixadate.domain.member.dto.request.MemberInfoUpdateRequest;
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

	@Transactional
	public MemberInfoResponse updateMemberInfo(String memberId, MemberInfoUpdateRequest memberInfoUpdateRequest) {
		Member member = memberRepository.findMemberById(memberId).orElseThrow(() -> new MemberNotFoundException(
			ExceptionCode.NOT_FOUND_MEMBER_ID));

		String url = null;
		if (member.updateMember(memberInfoUpdateRequest)) {
			System.out.println("URL 변경 되어야 함.");
			url = s3Util.generatePresignedUrlForUpload(member.getProfileImg(), memberInfoUpdateRequest.contentType());
		}
		return MemberMapper.toInfoResponse(member, url);
	}

	public String getRandomNickname(List<String> strings) {
		return strings.get(random.nextInt(strings.size())).trim();
	}
}
