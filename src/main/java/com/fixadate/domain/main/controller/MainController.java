package com.fixadate.domain.main.controller;

import com.fixadate.domain.main.dto.request.StoryBoardUpdate;
import com.fixadate.domain.main.dto.response.MainInfoResponse;
import com.fixadate.domain.main.service.MainService;
import com.fixadate.global.annotation.RestControllerWithMapping;
import com.fixadate.global.dto.GeneralResponseDto;
import com.fixadate.global.jwt.MemberPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestControllerWithMapping("/v1/main")
@RestController
@RequiredArgsConstructor
public class MainController {
	private final MainService mainService;

	@Operation(summary = "메인 정보 조회", description = "")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "정보 조회",
			content = @Content(schema = @Schema(implementation = MainInfoResponse.class)))
	})
	@GetMapping("/info")
	public GeneralResponseDto getMainInfo(
		@AuthenticationPrincipal final MemberPrincipal memberPrincipal,
		@RequestParam String yyyyMM,
		@RequestParam int weekNum
	) {
		// 앞 뒤 6주
		return GeneralResponseDto.success("", mainService.getMainInfo(memberPrincipal.getMember(), yyyyMM, weekNum));
	}

	@Operation(summary = "스토리보드 조회", description = "")
	@ApiResponses({
		@ApiResponse(
			responseCode = "200", description = "ok",
			content = @Content(schema = @Schema(implementation = String.class)))
	})
	@GetMapping("/board")
	public GeneralResponseDto getStoryBoard(
		@Parameter(description = "회원 정보") @AuthenticationPrincipal MemberPrincipal memberPrincipal
	){
		return GeneralResponseDto.success("", mainService.getStoryBoard(memberPrincipal.getMember()));
	}
	
	@Operation(summary = "스토리보드 등록/수정", description = "")
	@ApiResponses({
		@ApiResponse(
			responseCode = "200", description = "ok",
			content = @Content(schema = @Schema(implementation = String.class)))
	})
	@PostMapping("/board")
	public GeneralResponseDto updateStoryBoard(
		@Parameter(description = "회원 정보") @AuthenticationPrincipal MemberPrincipal memberPrincipal,
		@RequestBody StoryBoardUpdate storyBoardUpdate
	){
		return GeneralResponseDto.success("", mainService.updateStoryBoard(memberPrincipal.getMember(), storyBoardUpdate.storyBoard()));
	}
}
