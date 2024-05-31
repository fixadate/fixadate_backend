package com.fixadate.global.auth.controller.impl;

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

import com.fixadate.global.annotation.RestControllerWithMapping;
import com.fixadate.global.auth.controller.AuthController;
import com.fixadate.global.auth.dto.request.MemberOAuthRequest;
import com.fixadate.global.auth.dto.request.MemberRegistRequest;
import com.fixadate.global.auth.dto.response.MemberSigninResponse;
import com.fixadate.global.auth.service.AuthService;
import com.fixadate.global.jwt.entity.TokenResponse;
import com.fixadate.global.jwt.service.JwtProvider;
import com.fixadate.global.util.S3Util;

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
	private final S3Util s3Util;
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
	public ResponseEntity<String> signup(
		@Valid @RequestBody MemberRegistRequest memberRegistRequest) {
		authService.registMember(memberRegistRequest);

		String url = s3Util.generatePresignedUrlForUpload(memberRegistRequest.profileImg(),
			memberRegistRequest.contentType());
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(url);
	}

	@Override
	@PostMapping("/reissue")
	public ResponseEntity<Void> reissueAccessToken(
		@CookieValue(value = "refreshToken", required = true) Cookie cookie) {
		TokenResponse tokenResponse = jwtProvider.reIssueToken(cookie);
		ResponseCookie ncookie = authService.createHttpOnlyCooke(tokenResponse.getRefreshToken());

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(HttpHeaders.SET_COOKIE, ncookie.toString());
		httpHeaders.add(ACCESS_TOKEN.getValue(), tokenResponse.getAccessToken());

		return ResponseEntity.noContent()
			.headers(httpHeaders)
			.build();
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(HttpServletRequest httpServletRequest) {
		String accessToken = jwtProvider.retrieveToken(httpServletRequest);
		jwtProvider.memberLogout(accessToken);

		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/signout")
	public ResponseEntity<Void> signout(@RequestParam String email, HttpServletRequest httpServletRequest) {
		String accessToken = jwtProvider.retrieveToken(httpServletRequest);
		authService.memberSignout(email, jwtProvider.getIdFromToken(accessToken));
		jwtProvider.memberLogout(accessToken);

		return ResponseEntity.noContent().build();
	}
}
