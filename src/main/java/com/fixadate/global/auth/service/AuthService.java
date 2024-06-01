package com.fixadate.global.auth.service;

import static com.fixadate.domain.member.mapper.MemberMapper.*;
import static com.fixadate.global.auth.entity.OAuthProvider.*;
import static com.fixadate.global.exception.ExceptionCode.*;
import static com.fixadate.global.util.constant.ConstantValue.*;

import java.util.Optional;

import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.repository.MemberRepository;
import com.fixadate.domain.tag.entity.Tag;
import com.fixadate.domain.tag.repository.TagRepository;
import com.fixadate.global.auth.dto.request.MemberOAuthRequest;
import com.fixadate.global.auth.dto.request.MemberRegistRequest;
import com.fixadate.global.auth.dto.response.MemberSigninResponse;
import com.fixadate.global.auth.entity.OAuthProvider;
import com.fixadate.global.exception.notFound.MemberNotFoundException;
import com.fixadate.global.exception.unAuthorized.AuthException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
	private final MemberRepository memberRepository;
	private final TagRepository tagRepository;
	private final PasswordEncoder passwordEncoder;

	public MemberSigninResponse memberSignIn(MemberOAuthRequest memberOAuthRequest) {
		Member member = findMemberByOAuthProviderAndEmailAndName(
			translateStringToOAuthProvider(memberOAuthRequest.oauthPlatform()),
			memberOAuthRequest.email(), memberOAuthRequest.memberName()
		).orElseThrow(() -> new AuthException(NOT_FOUND_MEMBER_OAUTHPLATFORM_EMAIL_NAME));
		authenticateMember(memberOAuthRequest, member);
		return toResponse(member);
	}

	private Optional<Member> findMemberByOAuthProviderAndEmailAndName(OAuthProvider oauthProvider, String email,
		String name) {
		return memberRepository.findMemberByOauthPlatformAndEmailAndName(oauthProvider, email, name);
	}

	private void authenticateMember(MemberOAuthRequest memberOAuthRequest, Member member) {
		if (!passwordEncoder.matches(memberOAuthRequest.oauthId(), member.getOauthId())) {
			throw new AuthException(FAIL_TO_SIGNIN);
		}
	}

	@Transactional
	public void registMember(MemberRegistRequest memberRegistRequest) {
		Optional<Member> memberOptional = findMemberByOAuthProviderAndEmailAndName(
			translateStringToOAuthProvider(memberRegistRequest.oauthPlatform()),
			memberRegistRequest.email(), memberRegistRequest.name()
		);
		if (memberOptional.isPresent()) {
			throw new AuthException(ALREADY_EXISTS_MEMBER);
		}
		String encodedOauthId = passwordEncoder.encode(memberRegistRequest.oauthId());
		Member member = toEntity(memberRegistRequest, encodedOauthId);
		member.createMemberId();
		memberRepository.save(member);

		registGoogleCalendarTag(member);
	}

	public void registGoogleCalendarTag(Member member) {
		Tag tag = Tag.builder()
			.color(GOOGLE_CALENDAR_COLOR.getValue())
			.name(GOOGLE_CALENDAR.getValue())
			.isDefault(true)
			.member(member)
			.build();

		tagRepository.save(tag);
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
