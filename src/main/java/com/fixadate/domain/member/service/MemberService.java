package com.fixadate.domain.member.service;

import static com.fixadate.global.exception.ExceptionCode.*;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fixadate.domain.member.dto.request.MemberInfoUpdateRequest;
import com.fixadate.domain.member.dto.response.MemberInfoResponse;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.mapper.MemberMapper;
import com.fixadate.domain.member.repository.MemberRepository;
import com.fixadate.global.exception.notFound.MemberNotFoundException;
import com.fixadate.global.exception.notFound.S3ImgNotFound;
import com.fixadate.global.util.S3Util;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.S3Client;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
	private final MemberRepository memberRepository;
	private final S3Util s3Util;
	private final Random random = new Random(System.currentTimeMillis());
	private final ObjectProvider<MemberService> memberServices;
	private final S3Client s3Client;

	@Value("${cloud.aws.bucket-name}")
	private String bucket;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return null;
	}

	public MemberInfoResponse getMemberInfo(String memberId) {
		Member member = memberRepository.findMemberById(memberId).orElseThrow(() -> new MemberNotFoundException(
			NOT_FOUND_MEMBER_ID));
		return MemberMapper.toInfoResponse(member, s3Util.generatePresignedUrlForDownload(member.getProfileImg()));
	}

	@Transactional
	public MemberInfoResponse updateMemberInfo(String memberId, MemberInfoUpdateRequest memberInfoUpdateRequest) {
		Member member = memberRepository.findMemberById(memberId).orElseThrow(() -> new MemberNotFoundException(
			NOT_FOUND_MEMBER_ID));

		String url = null;
		String profileImg = member.getProfileImg();
		if (member.updateMember(memberInfoUpdateRequest)) {
			try {
				memberServices.getObject().deleteS3Img(profileImg);
			} catch (Exception e) {
				System.out.println("여기 보세요!!!");
				throw new S3ImgNotFound(NOT_FOUND_S3_IMG);
			}
			url = s3Util.generatePresignedUrlForUpload(member.getProfileImg());
		}
		return MemberMapper.toInfoResponse(member, url);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void deleteS3Img(String profileImg) {
		s3Client.deleteObject(builder -> builder.bucket(bucket).key(profileImg));
	}

	public String getRandomNickname(List<String> strings) {
		return strings.get(random.nextInt(strings.size())).trim();
	}
}
