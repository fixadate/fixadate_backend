package com.fixadate.domain.member.controller;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
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
}
