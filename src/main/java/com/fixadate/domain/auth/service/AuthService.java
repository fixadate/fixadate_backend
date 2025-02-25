package com.fixadate.domain.auth.service;

import static com.fixadate.domain.auth.entity.OAuthProvider.translateStringToOAuthProvider;
import static com.fixadate.domain.member.mapper.MemberMapper.toEntity;
import static com.fixadate.domain.member.mapper.MemberMapper.toMemberPlansEntity;
import static com.fixadate.domain.member.mapper.MemberMapper.toResponse;
import static com.fixadate.global.exception.ExceptionCode.ALREADY_EXISTS_MEMBER;
import static com.fixadate.global.exception.ExceptionCode.FAIL_TO_SIGNIN;
import static com.fixadate.global.exception.ExceptionCode.NOT_FOUND_MEMBER_EMAIL;
import static com.fixadate.global.exception.ExceptionCode.NOT_FOUND_MEMBER_ID;
import static com.fixadate.global.exception.ExceptionCode.NOT_FOUND_MEMBER_OAUTHPLATFORM_EMAIL_NAME;
import static com.fixadate.global.util.constant.ConstantValue.REFRESH_TOKEN;

import com.fixadate.domain.member.entity.MemberPlans;
import com.fixadate.domain.member.entity.Plans;
import com.fixadate.domain.member.entity.Plans.PlanType;
import com.fixadate.domain.member.service.repository.MemberPlansRepository;
import com.fixadate.domain.member.service.repository.PlansRepository;
import java.util.Arrays;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fixadate.domain.auth.dto.request.MemberOAuthRequest;
import com.fixadate.domain.auth.dto.request.MemberRegisterRequest;
import com.fixadate.domain.auth.dto.response.MemberSigninResponse;
import com.fixadate.domain.auth.dto.response.MemberSignupResponse;
import com.fixadate.domain.auth.entity.OAuthProvider;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.service.repository.MemberRepository;
import com.fixadate.domain.tag.event.object.TagMemberSettingEvent;
import com.fixadate.global.exception.notfound.MemberNotFoundException;
import com.fixadate.global.exception.unauthorized.AuthException;
import com.fixadate.global.util.S3Util;
import com.fixadate.global.util.constant.ExternalCalendar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
	private final MemberRepository memberRepository;
	private final MemberPlansRepository memberPlansRepository;
	private final PlansRepository plansRepository;
	private final ApplicationEventPublisher applicationEventPublisher;
	private final PasswordEncoder passwordEncoder;
	private final S3Util s3Util;

	public MemberSigninResponse memberSignIn(MemberOAuthRequest memberOAuthRequest) {
		Member member = findMemberByOAuthProviderAndEmailAndName(
			translateStringToOAuthProvider(memberOAuthRequest.oauthPlatform()),
			memberOAuthRequest.email(), memberOAuthRequest.memberName()
		).orElseThrow(() -> new AuthException(NOT_FOUND_MEMBER_OAUTHPLATFORM_EMAIL_NAME));
		authenticateMember(memberOAuthRequest, member);
		return toResponse(member);
	}

	private Optional<Member> findMemberByOAuthProviderAndEmailAndName(
		OAuthProvider oauthProvider, String email,
		String name
	) {
		return memberRepository.findMemberByOauthPlatformAndEmailAndName(oauthProvider, email, name);
	}

	private void authenticateMember(MemberOAuthRequest memberOAuthRequest, Member member) {
		if (!passwordEncoder.matches(memberOAuthRequest.oauthId(), member.getOauthId())) {
			throw new AuthException(FAIL_TO_SIGNIN);
		}
	}

	@Transactional
	public MemberSignupResponse registerMember(MemberRegisterRequest memberRegisterRequest) {
		Optional<Member> memberOptional = findMemberByOAuthProviderAndEmailAndName(
			translateStringToOAuthProvider(memberRegisterRequest.oauthPlatform()),
			memberRegisterRequest.email(), memberRegisterRequest.name()
		);
		if (memberOptional.isPresent()) {
			throw new AuthException(ALREADY_EXISTS_MEMBER);
		}
		String encodedOauthId = passwordEncoder.encode(memberRegisterRequest.oauthId());
		Member member = toEntity(memberRegisterRequest, encodedOauthId);
		memberRepository.save(member);

//		Plans freePlan = plansRepository.findPlansByName(PlanType.FREE).orElseThrow(
//			() -> new RuntimeException("")
//		);
//
//		MemberPlans memberPlans = toMemberPlansEntity(member, freePlan);
//		memberPlansRepository.save(memberPlans);

		registerExternalCalendarTag(member);

		return new MemberSignupResponse(s3Util.generatePresignedUrlForUpload(member.getProfileImg()));
	}

	private void registerExternalCalendarTag(final Member member) {
		Arrays.stream(ExternalCalendar.values())
			  .forEach(calendar -> applicationEventPublisher.publishEvent(new TagMemberSettingEvent(member, calendar)));
	}

	@Transactional
	public void memberSignout(String email, String id) {
		Member member = memberRepository.findMemberById(id)
										.orElseThrow(() -> new MemberNotFoundException(NOT_FOUND_MEMBER_ID));

		if (!member.getEmail().equals(email)) {
			throw new MemberNotFoundException(NOT_FOUND_MEMBER_EMAIL);
		}
		memberRepository.delete(member);
	}

	public ResponseCookie createHttpOnlyCooke(String token) {
		return ResponseCookie.from(REFRESH_TOKEN.getValue(), token)
							 .secure(true)
							 .httpOnly(true)
							 .build();
	}
}
