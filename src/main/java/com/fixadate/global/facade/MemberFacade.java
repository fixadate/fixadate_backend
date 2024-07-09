package com.fixadate.global.facade;

import static com.fixadate.global.exception.ExceptionCode.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fixadate.domain.googleCalendar.entity.GoogleCredentials;
import com.fixadate.domain.googleCalendar.repository.GoogleRepository;
import com.fixadate.domain.member.dto.request.MemberInfoUpdateRequest;
import com.fixadate.domain.member.dto.response.MemberInfoResponse;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.mapper.MemberMapper;
import com.fixadate.domain.member.repository.MemberRepository;
import com.fixadate.domain.pushKey.entity.PushKey;
import com.fixadate.global.exception.notFound.GoogleNotFoundException;
import com.fixadate.global.exception.notFound.MemberNotFoundException;
import com.fixadate.global.util.S3Util;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.S3Client;

/**
 *
 * @author yongjunhong
 * @since 2024. 7. 9.
 */

@Component
@RequiredArgsConstructor
public class MemberFacade {
	private final MemberRepository memberRepository;
	private final GoogleRepository googleRepository;
	private final S3Util s3Util;
	private final S3Client s3Client;

	@Value("${cloud.aws.bucket-name}")
	private String bucket;

	public GoogleCredentials getGoogleCredentialsByMemberId(String memberId) {
		Member member = memberRepository.findMemberById(memberId).orElseThrow(() -> new MemberNotFoundException(
			NOT_FOUND_MEMBER_ID));

		return googleRepository.findGoogleCredentialsByMember(member)
			.orElseThrow(() -> new GoogleNotFoundException(NOT_FOUND_GOOGLE_CREDENTIALS_MEMBER));

	}

	public void setMemberGoogleCredentials(String memberId, GoogleCredentials googleCredentials) {
		Member member = memberRepository.findMemberById(memberId).orElseThrow(() -> new MemberNotFoundException(
			NOT_FOUND_MEMBER_ID));

		member.setGoogleCredentials(googleCredentials);
	}

	public PushKey getPushKeyAndRegisterMember(String memberId, String pushKey) {
		Member member = memberRepository.findMemberById(memberId).orElseThrow(() -> new MemberNotFoundException(
			NOT_FOUND_MEMBER_ID));

		PushKey newPushKey = PushKey.builder()
			.memberId(memberId)
			.pushKey(pushKey)
			.build();

		member.setMemberPushKey(newPushKey);
		return newPushKey;
	}

	@Transactional
	public MemberInfoResponse deleteAndGetUploadUrl(String memberId, MemberInfoUpdateRequest memberInfoUpdateRequest) {
		Member member = memberRepository.findMemberById(memberId).orElseThrow(() -> new MemberNotFoundException(
			NOT_FOUND_MEMBER_ID));

		String url = null;
		String profileImg = member.getProfileImg();
		if (member.updateMember(memberInfoUpdateRequest)) {
			try {
				s3Client.deleteObject(builder -> builder.bucket(bucket).key(profileImg));
			} catch (Exception e) {
				throw new MemberNotFoundException(NOT_FOUND_MEMBER_ID);
			}
			url = s3Util.generatePresignedUrlForUpload(member.getProfileImg());
		}
		return MemberMapper.toInfoResponse(member, url);
	}

	public MemberInfoResponse getMemberInfo(String memberId) {
		Member member = memberRepository.findMemberById(memberId).orElseThrow(() -> new MemberNotFoundException(
			NOT_FOUND_MEMBER_ID));
		return MemberMapper.toInfoResponse(member, s3Util.generatePresignedUrlForDownload(member.getProfileImg()));
	}
}
