package com.fixadate.domain.dates.controller;


import com.fixadate.domain.dates.dto.request.TeamCreateRequest;
import com.fixadate.domain.dates.dto.response.TeamListPageResponse;
import com.fixadate.domain.dates.entity.Teams;
import com.fixadate.domain.dates.service.TeamService;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.dto.GeneralResponseDto;
import com.fixadate.global.factory.PageFactory;
import com.fixadate.global.jwt.MemberPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/teams")
public class TeamController {

    private final TeamService teamService;

    @Autowired
    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @Operation(summary = "Team 생성", description = "성공시 true, 실패시 false")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", description = "ok",
            content = @Content(schema = @Schema(implementation = Boolean.class)))
    })
    @PostMapping
    public GeneralResponseDto createTeam(@AuthenticationPrincipal final MemberPrincipal memberPrincipal,
                                            @RequestBody TeamCreateRequest request) {
        final Member member = memberPrincipal.getMember();
        Teams createdTeam = teamService.createTeam(member, request);
        return GeneralResponseDto.success("", createdTeam!=null);
    }

    @Operation(summary = "Team 삭제", description = "성공시 true, 실패시 false")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", description = "ok",
            content = @Content(schema = @Schema(implementation = Boolean.class)))
    })
    @DeleteMapping("/{id}")
    public GeneralResponseDto deleteTeam(@AuthenticationPrincipal final MemberPrincipal memberPrincipal,
                                              @PathVariable Long id) {
        final Member member = memberPrincipal.getMember();
        boolean result = teamService.deleteTeam(member, id);
        return GeneralResponseDto.success("", result);
    }

    @Operation(summary = "내 Team 목록 조회", description = "")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", description = "ok",
            content = @Content(schema = @Schema(implementation = TeamListPageResponse.class)))
    })
    @GetMapping
    public GeneralResponseDto getTeams(@AuthenticationPrincipal final MemberPrincipal memberPrincipal,
        @RequestParam("page") int page,
        @RequestParam("size") int size) {
        final Member member = memberPrincipal.getMember();
        Pageable newPageable = PageFactory.getPageableSortBy(page, size, "createDate", false);
        return GeneralResponseDto.success("", teamService.getTeams(member, newPageable));
    }

    @Operation(summary = "팀원 추방", description = "성공시 true, 실패시 false")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", description = "ok",
            content = @Content(schema = @Schema(implementation = Boolean.class)))
    })
    @DeleteMapping("/members/{teamMemberId}")
    public GeneralResponseDto deleteTeamMember(@AuthenticationPrincipal final MemberPrincipal memberPrincipal,
        @PathVariable Long teamMemberId) {
        final Member member = memberPrincipal.getMember();
        boolean result = teamService.deleteTeamMember(member, teamMemberId);
        return GeneralResponseDto.success("", result);
    }
}

