package com.fixadate.domain.pushkey.controller;

import com.fixadate.global.dto.GeneralResponseDto;
import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fixadate.global.jwt.MemberPrincipal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "PushKeyController", description = "PushKeyController 입니다.")
public interface PushKeyController {

	@Operation(summary = "pushKey 생성", description = "PushKey를 데이터베이스에서 조회한 뒤, 있으면 업데이트 없으면 생성합니다.")
	@Parameters(value = {
		@Parameter(name = "accessToken", description = "Authorization : Bearer + <jwt>", in = ParameterIn.HEADER),
		@Parameter(name = "pushKey", description = "pushKey 값입니다.",
			content = @Content(schema = @Schema(implementation = String.class)), in = ParameterIn.QUERY)
	})
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "No Content",
			content = @Content(schema = @Schema(implementation = Void.class))),
		@ApiResponse(responseCode = "401", description = "jwt 만료되었을 때 생기는 예외",
			content = @Content(schema = @Schema(implementation = Void.class))),
		@ApiResponse(responseCode = "404", description = "securityContext에 있는 값으로 member를 찾을 수 없는 경우",
			content = @Content(schema = @Schema(implementation = Void.class)))
	})
	GeneralResponseDto registPushKey(@AuthenticationPrincipal MemberPrincipal memberPrincipal,
		@RequestParam String pushKey);

	@Operation(summary = "pushKey test", description = "PushKey를 넣으면 푸시를 보내는 테스트입니다.")
	@Parameters(value = {
		@Parameter(name = "pushKey", description = "pushKey 값입니다.",
			content = @Content(schema = @Schema(implementation = String.class)), in = ParameterIn.QUERY)
	})
	@PostMapping("/test")
	GeneralResponseDto testFcm(@RequestParam String pushKey) throws IOException;

	@Operation(summary = "pushKey test", description = "PushKey와 이미지를 넣으면 이미지와 푸시를 보내는 테스트입니다. 또한 test용 data도 같이 보냅니다. 테스트용으로 네이버 링크 이동")
	@Parameters(value = {
		@Parameter(name = "pushKey", description = "pushKey 값입니다.",
			content = @Content(schema = @Schema(implementation = String.class)), in = ParameterIn.QUERY),
		@Parameter(name = "image", description = "이미지 uri입니다.",
			content = @Content(schema = @Schema(implementation = String.class)), in = ParameterIn.QUERY)
	})
	@PostMapping("/test-image")
	GeneralResponseDto testFcmWithData(@RequestParam String pushKey, @RequestParam String image) throws IOException;
}
