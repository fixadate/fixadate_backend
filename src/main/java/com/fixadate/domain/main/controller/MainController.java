package com.fixadate.domain.main.controller;

import com.fixadate.domain.main.dto.request.StoryBoardUpdate;
import com.fixadate.domain.main.dto.response.MainInfoResponse;
import com.fixadate.global.dto.GeneralResponseDto;
import com.fixadate.global.jwt.MemberPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface MainController {
    @Operation(summary = "메인 정보 조회", description = "")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정보 조회",
                    content = @Content(schema = @Schema(implementation = MainInfoResponse.class)))
    })
    GeneralResponseDto getMainInfo(
            @AuthenticationPrincipal final MemberPrincipal memberPrincipal,
            @RequestParam String yyyyMM,
            @RequestParam int weekNum
    );

    @Operation(summary = "스토리보드 조회", description = "")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "ok",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    GeneralResponseDto getStoryBoard(
            @Parameter(description = "회원 정보") @AuthenticationPrincipal MemberPrincipal memberPrincipal
    );

    @Operation(summary = "스토리보드 등록/수정", description = "")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "ok",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    GeneralResponseDto updateStoryBoard(
            @Parameter(description = "회원 정보") @AuthenticationPrincipal MemberPrincipal memberPrincipal,
            @RequestBody StoryBoardUpdate storyBoardUpdate
    );
}
