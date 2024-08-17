package com.fixadate.global.facade;

import static com.fixadate.domain.member.mapper.MemberMapper.toInfoDto;
import static com.fixadate.global.exception.ExceptionCode.NOT_FOUND_GOOGLE_CREDENTIALS_MEMBER;
import static com.fixadate.global.exception.ExceptionCode.NOT_FOUND_MEMBER_ID;
import static com.fixadate.global.exception.ExceptionCode.NOT_FOUND_S3_IMG;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fixadate.domain.googlecalendar.entity.GoogleCredentials;
import com.fixadate.domain.googlecalendar.repository.GoogleRepository;
import com.fixadate.domain.member.dto.request.MemberInfoUpdateDto;
import com.fixadate.domain.member.dto.response.MemberInfoDto;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.service.repository.MemberRepository;
import com.fixadate.domain.pushkey.entity.PushKey;
import com.fixadate.global.exception.notfound.GoogleNotFoundException;
import com.fixadate.global.exception.notfound.MemberNotFoundException;
import com.fixadate.global.exception.notfound.S3ImgNotFound;
import com.fixadate.global.util.S3Util;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.S3Client;

@Component
@RequiredArgsConstructor
public class MemberFacade {

	private final MemberRepository memberRepository;
	private final GoogleRepository googleRepository;
	private final S3Util s3Util;
	private final S3Client s3Client;

	@Value("${cloud.aws.bucket-name}")
	private String bucket;

	public GoogleCredentials getGoogleCredentialsByMemberId(final String memberId) {
		Member member = memberRepository.findMemberById(memberId).orElseThrow(
			() -> new MemberNotFoundException(NOT_FOUND_MEMBER_ID)
		);

		return googleRepository.findGoogleCredentialsByMember(member).orElseThrow(
			() -> new GoogleNotFoundException(NOT_FOUND_GOOGLE_CREDENTIALS_MEMBER)
		);
	}

	public void setMemberGoogleCredentials(final String memberId, final GoogleCredentials googleCredentials) {
		Member member = memberRepository.findMemberById(memberId).orElseThrow(
			() -> new MemberNotFoundException(NOT_FOUND_MEMBER_ID)
		);

		member.updateGoogleCredentials(googleCredentials);
	}

	public PushKey getPushKeyAndRegisterMember(final String memberId, final String pushKey) {
		Member member = memberRepository.findMemberById(memberId).orElseThrow(
			() -> new MemberNotFoundException(NOT_FOUND_MEMBER_ID)
		);

		PushKey newPushKey = PushKey.builder()
									.memberId(memberId)
									.pushKey(pushKey)
									.build();
		member.updateMemberPushKey(newPushKey);

		return newPushKey;
	}

	@Transactional
	public MemberInfoDto deleteAndGetUploadUrl(final MemberInfoUpdateDto memberInfoUpdateDto) {
		Member member = memberRepository.findMemberById(memberInfoUpdateDto.memberId()).orElseThrow(
			() -> new MemberNotFoundException(NOT_FOUND_MEMBER_ID)
		);

		updateMemberInfo(member, memberInfoUpdateDto);
		String url = handleProfileImageUpdate(member, memberInfoUpdateDto);

		return toInfoDto(member, url);
	}

	private void updateMemberInfo(final Member member, final MemberInfoUpdateDto memberInfoUpdateDto) {
		if (memberInfoUpdateDto.nickname() != null) {
			member.updateNickname(memberInfoUpdateDto.nickname());
		}

		if (memberInfoUpdateDto.signatureColor() != null) {
			member.updateSignatureColor(memberInfoUpdateDto.signatureColor());
		}

		if (memberInfoUpdateDto.profession() != null) {
			member.updateProfession(memberInfoUpdateDto.profession());
		}
	}

	private String handleProfileImageUpdate(final Member member, final MemberInfoUpdateDto memberInfoUpdateDto) {
		String url = null;

		if (memberInfoUpdateDto.profileImg() != null) {
			deleteCurrentProfileImage(member.getProfileImg());
			url = s3Util.generatePresignedUrlForUpload(memberInfoUpdateDto.profileImg());
			member.updateProfileImg(memberInfoUpdateDto.profileImg());
		}

		return url;
	}

	private void deleteCurrentProfileImage(final String profileImg) {
		try {
			s3Client.deleteObject(builder -> builder.bucket(bucket).key(profileImg));
		} catch (Exception e) {
			throw new S3ImgNotFound(NOT_FOUND_S3_IMG);
		}
	}

	public MemberInfoDto getMemberInfo(final String memberId) {
		Member member = memberRepository.findMemberById(memberId).orElseThrow(
			() -> new MemberNotFoundException(NOT_FOUND_MEMBER_ID)
		);

		return toInfoDto(member, s3Util.generatePresignedUrlForDownload(member.getProfileImg()));
	}
}
