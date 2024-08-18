package com.fixadate.domain.member.mapper;

import static com.fixadate.domain.auth.entity.OAuthProvider.translateStringToOAuthProvider;

import com.fixadate.domain.auth.dto.request.MemberRegisterRequest;
import com.fixadate.domain.auth.dto.response.MemberSigninResponse;
import com.fixadate.domain.member.dto.request.MemberInfoUpdateDto;
import com.fixadate.domain.member.dto.request.MemberInfoUpdateRequest;
import com.fixadate.domain.member.dto.response.MemberInfoDto;
import com.fixadate.domain.member.dto.response.MemberInfoResponse;
import com.fixadate.domain.member.entity.Member;

public class MemberMapper {

	private MemberMapper() {
	}

	public static Member toEntity(final MemberRegisterRequest request, final String oauthId) {
		return Member.builder()
					 .oauthId(oauthId)
					 .oauthPlatform(translateStringToOAuthProvider(request.oauthPlatform()))
					 .name(request.name())
					 .profileImg(request.getProfileImg())
					 .nickname(request.nickname())
					 .birth(request.birth())
					 .gender(request.gender())
					 .email(request.email())
					 .profession(request.profession())
					 .signatureColor(request.signatureColor())
					 .role(request.role())
					 .build();

	}

	public static MemberSigninResponse toResponse(final Member member) {
		return new MemberSigninResponse(
			member.getId(),
			member.getName(),
			member.getNickname(),
			member.getBirth(),
			member.getGender(),
			member.getProfession(),
			member.getSignatureColor(),
			member.getEmail()
		);
	}

	public static MemberInfoResponse toInfoResponse(final MemberInfoDto memberInfoDto) {
		return new MemberInfoResponse(
			memberInfoDto.name(),
			memberInfoDto.nickname(),
			memberInfoDto.birth(),
			memberInfoDto.gender(),
			memberInfoDto.signatureColor(),
			memberInfoDto.profession(),
			memberInfoDto.url()
		);
	}

	public static MemberInfoDto toInfoDto(final Member member, final String url) {
		return new MemberInfoDto(
			member.getOauthPlatform(),
			member.getName(),
			member.getProfileImg(),
			member.getNickname(),
			member.getBirth(),
			member.getGender(),
			member.getProfession(),
			member.getSignatureColor(),
			member.getEmail(),
			member.getPushKey(),
			member.getGoogleCredentials(),
			url
		);
	}

	public static MemberInfoUpdateDto toUpdateDto(
		final String memberId,
		final MemberInfoUpdateRequest memberInfoUpdateRequest
	) {
		return new MemberInfoUpdateDto(
			memberId,
			memberInfoUpdateRequest.nickname(),
			memberInfoUpdateRequest.signatureColor(),
			memberInfoUpdateRequest.profession(),
			memberInfoUpdateRequest.profileImg()
		);
	}
}
