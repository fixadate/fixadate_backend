package com.fixadate.domain.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

import com.fixadate.domain.member.dto.request.MemberInfoUpdateRequest;
import com.fixadate.domain.member.dto.response.MemberInfoResponse;
import com.fixadate.global.jwt.MemberPrincipal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "MemberController", description = "MemberController 입니다.")
public interface MemberController {

	@Operation(summary = "랜덤 닉네임 생성", description = "랜덤 닉네임을 생성합니다.")
	@ApiResponses({
		@ApiResponse(
			responseCode = "200", description = "ok",
			content = @Content(schema = @Schema(implementation = String.class)))
	})
	ResponseEntity<String> getRandomNickname();

	@Operation(summary = "회원 정보 조회", description = "회원의 정보를 조회합니다.")
	@ApiResponses({
		@ApiResponse(
			responseCode = "200", description = "ok",
			content = @Content(schema = @Schema(implementation = MemberInfoResponse.class)))
	})
	ResponseEntity<MemberInfoResponse> getMemberInfo(
		@Parameter(description = "회원 정보") @AuthenticationPrincipal MemberPrincipal memberPrincipal
	);

	@Operation(summary = "회원 정보 수정", description = "회원의 정보를 수정합니다.")
	@ApiResponses({
		@ApiResponse(
			responseCode = "200", description = "ok",
			content = @Content(schema = @Schema(implementation = MemberInfoResponse.class)))
	})
	ResponseEntity<MemberInfoResponse> updateMemberInfo(
		@Parameter(description = "회원 정보") @AuthenticationPrincipal MemberPrincipal memberPrincipal,
		@Parameter(description = "수정할 회원 정보") @RequestBody MemberInfoUpdateRequest memberInfoUpdateRequest
	);
}
