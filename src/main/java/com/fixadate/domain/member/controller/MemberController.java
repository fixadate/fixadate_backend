package com.fixadate.domain.member.controller;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "MemberController", description = "MemberController 입니다.")
public interface MemberController {

	@Operation(summary = "랜덤 닉네임 생성", description = "랜덤 닉네임을 생성합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "ok",
			content = @Content(schema = @Schema(implementation = String.class)))
	})
	ResponseEntity<String> getRandomNickname();

	@Operation(summary = "이미지 조회용 Presigned URL 발급", description = "이미지 조회를 위햔 url 발급.")
	@Parameters(value = {
		@Parameter(name = "accessToken", description = "Authorization : Bearer + <jwt>", in = ParameterIn.HEADER),
		@Parameter(name = "filename", description = "amazon s3로부터 presignedURL을 받기 위한 filename",
			content = @Content(schema = @Schema(implementation = String.class)))
	})
	@ApiResponses(
		@ApiResponse(responseCode = "200", description = "presignedURL 발급 완료",
			content = @Content(schema = @Schema(implementation = String.class)))
	)
	ResponseEntity<String> getProfileImagePresignedUrl(String filename);

	@Operation(summary = "이미지 삭제용 Presigned 발급", description = "이미지 삭제를 위한 url 발급.")
	@Parameters(value = {
		@Parameter(name = "accessToken", description = "Authorization : Bearer + <jwt>", in = ParameterIn.HEADER),
		@Parameter(name = "filename", description = "이미지를 삭제하기 위해 amazons3에게 전달하는 filename",
			content = @Content(schema = @Schema(implementation = String.class)))
	})

	@ApiResponses(
		@ApiResponse(responseCode = "200", description = "presignedURL 발급 완료",
			content = @Content(schema = @Schema(implementation = String.class)))
	)
	ResponseEntity<String> getProfileImageDeletePresignedUrl(String filename);
}
