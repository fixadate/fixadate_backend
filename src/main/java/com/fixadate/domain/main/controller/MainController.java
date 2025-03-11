package com.fixadate.domain.main.controller;

import com.fixadate.domain.invitation.dto.response.InvitableMemberListResponse;
import com.fixadate.domain.main.dto.response.MainInfoResponse;
import com.fixadate.domain.main.service.MainService;
import com.fixadate.domain.member.dto.response.MemberInfoResponse;
import com.fixadate.global.dto.GeneralResponseDto;
import com.fixadate.global.jwt.MemberPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
