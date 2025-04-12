package com.fixadate.domain.dates.controller;

import com.fixadate.domain.dates.dto.request.TeamCreateRequest;
import com.fixadate.domain.dates.dto.response.TeamListPageResponse;
import com.fixadate.global.dto.GeneralResponseDto;
import com.fixadate.global.jwt.MemberPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

public interface TeamController {
    @Operation(summary = "Team 생성", description = "성공시 true, 실패시 false")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "ok",
                    content = @Content(schema = @Schema(implementation = Boolean.class)))
    })
    GeneralResponseDto createTeam(@AuthenticationPrincipal final MemberPrincipal memberPrincipal,
                                         @RequestBody TeamCreateRequest request);

    @Operation(summary = "Team 삭제", description = "성공시 true, 실패시 false")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "ok",
                    content = @Content(schema = @Schema(implementation = Boolean.class)))
    })
    GeneralResponseDto deleteTeam(@AuthenticationPrincipal final MemberPrincipal memberPrincipal,
                                         @PathVariable Long id);

    @Operation(summary = "내 Team 목록 조회", description = "")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "ok",
                    content = @Content(schema = @Schema(implementation = TeamListPageResponse.class)))
    })
    GeneralResponseDto getTeams(@AuthenticationPrincipal final MemberPrincipal memberPrincipal,
                                       @RequestParam("page") int page,
                                       @RequestParam("size") int size);

    @Operation(summary = "팀원 추방", description = "성공시 true, 실패시 false")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "ok",
                    content = @Content(schema = @Schema(implementation = Boolean.class)))
    })
    GeneralResponseDto deleteTeamMember(@AuthenticationPrincipal final MemberPrincipal memberPrincipal,
                                               @PathVariable Long teamMemberId);
}
