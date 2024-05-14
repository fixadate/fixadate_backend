package com.fixadate.unit.global.auth.service;

import static com.fixadate.unit.domain.member.fixture.MemberFixture.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fixadate.domain.member.repository.MemberRepository;
import com.fixadate.global.auth.dto.request.MemberOAuthRequest;
import com.fixadate.global.auth.dto.request.MemberRegistRequest;
import com.fixadate.global.auth.dto.response.MemberSigninResponse;
import com.fixadate.global.auth.service.AuthService;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

	@InjectMocks
	private AuthService authService;
	@Mock
	private MemberRepository memberRepository;
	@Mock
	private PasswordEncoder passwordEncoder;

	@DisplayName("로그인")
	@Test
	void signinTest() {
		MemberOAuthRequest oAuthRequest = new MemberOAuthRequest(
			MEMBER.getOauthId(),
			MEMBER.getName(),
			MEMBER.getEmail(),
			MEMBER.getOauthPlatform().getProvider()
		);
		given(memberRepository.findMemberByOauthPlatformAndEmailAndName(MEMBER.getOauthPlatform(), MEMBER.getEmail(),
			MEMBER.getName())).willReturn(Optional.of(MEMBER));

		given(passwordEncoder.matches(any(String.class), any(String.class))).willReturn(true);

		MemberSigninResponse response = authService.memberSignIn(oAuthRequest);

		assertEquals(MEMBER.getEmail(), response.email());
	}

	@DisplayName("회원 가입")
	@Test
	void registMemberTest() {
		MemberRegistRequest memberRegistRequest = new MemberRegistRequest(
			MEMBER.getOauthId(),
			MEMBER.getOauthPlatform().getProvider(),
			MEMBER.getName(),
			MEMBER.getProfileImg(),
			MEMBER.getNickname(),
			MEMBER.getBirth(),
			MEMBER.getGender(),
			MEMBER.getProfession(),
			MEMBER.getSignatureColor(),
			"image",
			MEMBER.getEmail(),
			MEMBER.getRole()
		);
		given(passwordEncoder.encode(any(String.class))).willReturn(MEMBER.getOauthId());

		assertDoesNotThrow(() -> authService.registMember(memberRegistRequest));
	}

	@DisplayName("탈퇴")
	@Test
	void signoutTest() {
		given(memberRepository.findMemberById(any(String.class))).willReturn(Optional.ofNullable(MEMBER));

		assertDoesNotThrow(() -> authService.memberSignout(MEMBER.getEmail(), MEMBER.getId()));
	}

}
