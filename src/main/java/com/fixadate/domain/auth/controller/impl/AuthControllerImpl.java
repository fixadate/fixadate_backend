package com.fixadate.domain.auth.controller.impl;

import static com.fixadate.global.util.constant.ConstantValue.*;

import com.fixadate.global.dto.GeneralResponseDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
	public ResponseEntity<GeneralResponseDto> signin(
			@Valid @RequestBody MemberOAuthRequest memberOAuthRequest,
			@RequestHeader HttpHeaders headers) {
		MemberSigninResponse response = authService.memberSignIn(memberOAuthRequest);

		TokenResponse tokenResponse = jwtProvider.getTokenResponse(response.id());

		ResponseCookie cookie = authService.createHttpOnlyCooke(tokenResponse.getRefreshToken());

		HttpHeaders responseHeaders = new HttpHeaders();

		responseHeaders.add(HttpHeaders.SET_COOKIE, cookie.toString());
		responseHeaders.add(ACCESS_TOKEN.getValue(), tokenResponse.getAccessToken());

		return ResponseEntity
			.status(HttpStatus.OK)
			.headers(responseHeaders)
			.body(GeneralResponseDto.success("", response));
	}

	@Override
	@PostMapping("/signup")
	public GeneralResponseDto signup(
		@Valid @RequestBody MemberRegisterRequest memberRegisterRequest) {

		MemberSignupResponse memberSignupResponse = authService.registerMember(memberRegisterRequest);
		return GeneralResponseDto.success("", memberSignupResponse);
	}

	@Override
	@PostMapping("/reissue")
	public GeneralResponseDto reissueAccessToken(
		@CookieValue(value = "refreshToken") Cookie cookie,
		@RequestHeader HttpHeaders headers) {
		TokenResponse tokenResponse = jwtProvider.reIssueToken(cookie);
		ResponseCookie ncookie = authService.createHttpOnlyCooke(tokenResponse.getRefreshToken());

		headers.add(HttpHeaders.SET_COOKIE, ncookie.toString());
		headers.add(ACCESS_TOKEN.getValue(), tokenResponse.getAccessToken());

		return GeneralResponseDto.success("", "");
	}

	@Override
	@PostMapping("/logout")
	public GeneralResponseDto logout(HttpServletRequest httpServletRequest) {
		String accessToken = jwtProvider.retrieveToken(httpServletRequest);
		jwtProvider.memberLogout(accessToken);

		return GeneralResponseDto.success("", "");
	}

	@Override
	@DeleteMapping("/signout")
	public GeneralResponseDto signout(@RequestParam String email, HttpServletRequest httpServletRequest) {
		String accessToken = jwtProvider.retrieveToken(httpServletRequest);
		authService.memberSignout(email, jwtProvider.getIdFromToken(accessToken));
		jwtProvider.memberLogout(accessToken);

		return GeneralResponseDto.success("", "");
	}
}
