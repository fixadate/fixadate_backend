package com.fixadate.domain.auth.controller.impl;

import static com.fixadate.global.util.constant.ConstantValue.*;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.fixadate.domain.auth.controller.AuthController;
import com.fixadate.domain.auth.dto.request.MemberOAuthRequest;
import com.fixadate.domain.auth.dto.request.MemberRegisterRequest;
import com.fixadate.domain.auth.dto.response.MemberSigninResponse;
import com.fixadate.domain.auth.dto.response.MemberSignupResponse;
import com.fixadate.domain.auth.service.AuthService;
import com.fixadate.global.annotation.RestControllerWithMapping;
import com.fixadate.global.jwt.entity.TokenResponse;
import com.fixadate.global.jwt.service.JwtProvider;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestControllerWithMapping("/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthControllerImpl implements AuthController {
	private final AuthService authService;
	private final JwtProvider jwtProvider;

	@Override
	@PostMapping("/signin")
	public ResponseEntity<MemberSigninResponse> signin(@Valid @RequestBody MemberOAuthRequest memberOAuthRequest) {
		MemberSigninResponse response = authService.memberSignIn(memberOAuthRequest);

		TokenResponse tokenResponse = jwtProvider.getTokenResponse(response.id());

		ResponseCookie cookie = authService.createHttpOnlyCooke(tokenResponse.getRefreshToken());

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(HttpHeaders.SET_COOKIE, cookie.toString());
		httpHeaders.add(ACCESS_TOKEN.getValue(), tokenResponse.getAccessToken());

		return ResponseEntity.status(HttpStatus.OK)
			.headers(httpHeaders)
			.body(response);
	}

	@Override
	@PostMapping("/signup")
	public ResponseEntity<MemberSignupResponse> signup(
		@Valid @RequestBody MemberRegisterRequest memberRegisterRequest) {

		MemberSignupResponse memberSignupResponse = authService.registerMember(memberRegisterRequest);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(memberSignupResponse);
	}

	@Override
	@PostMapping("/reissue")
	public ResponseEntity<Void> reissueAccessToken(
		@CookieValue(value = "refreshToken") Cookie cookie) {
		TokenResponse tokenResponse = jwtProvider.reIssueToken(cookie);
		ResponseCookie ncookie = authService.createHttpOnlyCooke(tokenResponse.getRefreshToken());

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(HttpHeaders.SET_COOKIE, ncookie.toString());
		httpHeaders.add(ACCESS_TOKEN.getValue(), tokenResponse.getAccessToken());

		return ResponseEntity.noContent()
			.headers(httpHeaders)
			.build();
	}

	@Override
	@PostMapping("/logout")
	public ResponseEntity<Void> logout(HttpServletRequest httpServletRequest) {
		String accessToken = jwtProvider.retrieveToken(httpServletRequest);
		jwtProvider.memberLogout(accessToken);

		return ResponseEntity.noContent().build();
	}

	@Override
	@DeleteMapping("/signout")
	public ResponseEntity<Void> signout(@RequestParam String email, HttpServletRequest httpServletRequest) {
		String accessToken = jwtProvider.retrieveToken(httpServletRequest);
		authService.memberSignout(email, jwtProvider.getIdFromToken(accessToken));
		jwtProvider.memberLogout(accessToken);

		return ResponseEntity.noContent().build();
	}
}
