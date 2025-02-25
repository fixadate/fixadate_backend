package com.fixadate.domain.auth.controller;

import com.fixadate.global.dto.GeneralResponseDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.fixadate.domain.auth.dto.request.MemberOAuthRequest;
import com.fixadate.domain.auth.dto.request.MemberRegisterRequest;
import com.fixadate.domain.auth.dto.response.MemberSigninResponse;
import com.fixadate.domain.auth.dto.response.MemberSignupResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Tag(name = "AuthController", description = "AuthController 입니다.")
public interface AuthController {

	@Operation(summary = "로그인", description = "OAuth 대조를 통해 로그인을 합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "로그인 성공",
			content = @Content(schema = @Schema(implementation = MemberSigninResponse.class)),
			headers = {
				@Header(name = "cookie", description = "refreshToken이 담겨있는 httpOnlyCookie입니다."),
				@Header(name = "accessToken", description = "accessToken입니다.")
			}),
		@ApiResponse(responseCode = "401", description = "로그인 실패",
			content = @Content(schema = @Schema(implementation = Void.class)))
	})
	ResponseEntity<GeneralResponseDto> signin(@Valid @RequestBody MemberOAuthRequest memberOAuthRequest, @RequestHeader HttpHeaders headers);

	@Operation(summary = "회원가입", description = "회원가입을 합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "회원 가입 성공, profile 이미지 등록을 위해 링크 발급",
			content = @Content(schema = @Schema(implementation = MemberSignupResponse.class))),
		@ApiResponse(responseCode = "400", description = "이미 member가 데이터베이스에 존재할 때",
			content = @Content(schema = @Schema(implementation = Void.class)))
	})
	GeneralResponseDto signup(@Valid @RequestBody MemberRegisterRequest memberRegisterRequest);

	@Operation(summary = "token 재발급", description = "accessToken, refreshToken 재발급")
	@Parameter(name = "accessToken", description = "Authorization : Bearer + <jwt>", in = ParameterIn.HEADER)
	@Parameters(
		value = {
			@Parameter(name = "refreshToken",
				description = "refreshToken : <cookie>", in = ParameterIn.COOKIE, required = true)
		})
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "재발급 성공", headers = {
			@Header(name = "refreshToken이 담긴 cookie", schema = @Schema(implementation = Cookie.class)),
			@Header(name = "accessToken", schema = @Schema(implementation = String.class))},
			content = @Content(schema = @Schema(implementation = Void.class))),
		@ApiResponse(responseCode = "401", description = "유효하지 않은 refreshToken",
			content = @Content(schema = @Schema(implementation = Void.class)))
	})
	GeneralResponseDto reissueAccessToken(@CookieValue(value = "refreshToken") Cookie cookie,
		 								  @RequestHeader HttpHeaders headers);

	@Operation(summary = "로그아웃", description = "refreshToken을 삭제하고, accessToken 블랙리스트")
	@Parameter(name = "accessToken", description = "Authorization : Bearer + <jwt>", in = ParameterIn.HEADER)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "로그아웃 성공",
			content = @Content(schema = @Schema(implementation = Void.class))),
		@ApiResponse(responseCode = "401", description = "jwt 만료되었을 때 생기는 예외",
			content = @Content(schema = @Schema(implementation = Void.class)))
	})
	GeneralResponseDto logout(HttpServletRequest httpServletRequest);

	@Operation(summary = "탈퇴",
		description = "member의 모든 정보를 지우고 연관 정보도 지웁니다., refreshToken 삭제, accessToken 블랙리스트")
	@Parameters(value = {
		@Parameter(name = "accessToken", description = "Authorization : Bearer + <jwt>", in = ParameterIn.HEADER),
		@Parameter(name = "email", required = true, description = "userEmail")
	})
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "탈퇴 성공",
			content = @Content(schema = @Schema(implementation = Void.class))),
		@ApiResponse(responseCode = "401", description = "jwt 만료되었을 때 생기는 예외",
			content = @Content(schema = @Schema(implementation = Void.class))),
		@ApiResponse(responseCode = "404", description = "member가 존재하지 않을 때 생기는 예외",
			content = @Content(schema = @Schema(implementation = Void.class)))
	})
	GeneralResponseDto signout(@RequestParam String email, HttpServletRequest httpServletRequest);
}
