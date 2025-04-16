package com.fixadate.domain.main.controller.impl;

import com.fixadate.domain.main.controller.MainController;
import com.fixadate.domain.main.dto.request.StoryBoardUpdate;
import com.fixadate.domain.main.service.MainService;
import com.fixadate.global.annotation.RestControllerWithMapping;
import com.fixadate.global.dto.GeneralResponseDto;
import com.fixadate.global.jwt.MemberPrincipal;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestControllerWithMapping("/v1/main")
@RequiredArgsConstructor
public class MainControllerImpl implements MainController {
	private final MainService mainService;

	@Override
	@GetMapping("/info")
	public GeneralResponseDto getMainInfo(
		@AuthenticationPrincipal final MemberPrincipal memberPrincipal,
		@RequestParam String yyyyMM,
		@RequestParam int weekNum
	) {
		// 앞 뒤 6주
		return GeneralResponseDto.success("", mainService.getMainInfo(memberPrincipal.getMember(), yyyyMM, weekNum));
	}

	@Override
	@GetMapping("/board")
	public GeneralResponseDto getStoryBoard(
		@Parameter(description = "회원 정보") @AuthenticationPrincipal MemberPrincipal memberPrincipal
	){
		return GeneralResponseDto.success("", mainService.getStoryBoard(memberPrincipal.getMember()));
	}

	@Override
	@PostMapping("/board")
	public GeneralResponseDto updateStoryBoard(
		@Parameter(description = "회원 정보") @AuthenticationPrincipal MemberPrincipal memberPrincipal,
		@RequestBody StoryBoardUpdate storyBoardUpdate
	){
		return GeneralResponseDto.success("", mainService.updateStoryBoard(memberPrincipal.getMember(), storyBoardUpdate.storyBoard()));
	}
}
