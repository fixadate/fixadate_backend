package com.fixadate.global.auth.service;

import static com.fixadate.global.auth.entity.OAuthProvider.*;

import java.util.Optional;

import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.exception.MemberNotFoundException;
import com.fixadate.domain.member.repository.MemberRepository;
import com.fixadate.global.auth.dto.request.MemberOAuthRequest;
import com.fixadate.global.auth.dto.request.MemberRegistRequest;
import com.fixadate.global.auth.dto.response.MemberSigninResponse;
import com.fixadate.global.auth.entity.OAuthProvider;
import com.fixadate.global.auth.exception.MemberSigninException;
import com.fixadate.global.auth.exception.MemberSignupException;
import com.fixadate.global.util.constant.ConstantValue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	public MemberSigninResponse memberSignIn(MemberOAuthRequest memberOAuthRequest) {
		Member member = findMemberByOAuthProviderAndEmailAndName(
			translateStringToOAuthProvider(memberOAuthRequest.oauthPlatform()),
			memberOAuthRequest.email(), memberOAuthRequest.memberName()
		).orElseThrow(() -> new MemberSigninException("Member not found"));
		authenticateMember(memberOAuthRequest, member);
		return MemberSigninResponse.of(member);
	}

	private Optional<Member> findMemberByOAuthProviderAndEmailAndName(OAuthProvider oauthProvider, String email,
		String name) {
		return memberRepository.findMemberByOauthPlatformAndEmailAndName(oauthProvider, email, name);
	}

	private void authenticateMember(MemberOAuthRequest memberOAuthRequest, Member member) {
		if (!passwordEncoder.matches(memberOAuthRequest.oauthId(), member.getOauthId())) {
			throw new MemberSigninException("Invalid credentials");
		}
	}

	@Transactional
	public void registMember(MemberRegistRequest memberRegistRequest) {
		Optional<Member> memberOptional = findMemberByOAuthProviderAndEmailAndName(
			translateStringToOAuthProvider(memberRegistRequest.oauthPlatform()),
			memberRegistRequest.email(), memberRegistRequest.name()
		);
		if (memberOptional.isPresent()) {
			throw new MemberSignupException();
		}
		String encodedOauthId = passwordEncoder.encode(memberRegistRequest.oauthId());
		//fixme modelmapper 사용해서 구현해보기
		Member member = memberRegistRequest.of(encodedOauthId);
		member.createMemberId();
		memberRepository.save(member);
	}

	@Transactional
	public void memberSignout(String email, String id) {
		Member member = memberRepository.findMemberById(id)
			.orElseThrow(MemberNotFoundException::new);

		if (!member.getEmail().equals(email)) {
			throw new MemberNotFoundException();
		}
		memberRepository.delete(member);
	}

	public ResponseCookie createHttpOnlyCooke(String token) {
		return ResponseCookie.from(ConstantValue.REFRESH_TOKEN.getValue(), token)
			.secure(true)
			.httpOnly(true)
			.build();
	}
}
