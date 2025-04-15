package com.fixadate.domain.dates.controller.impl;

import com.fixadate.domain.dates.controller.TeamController;
import com.fixadate.domain.dates.dto.request.TeamCreateRequest;
import com.fixadate.domain.dates.entity.Teams;
import com.fixadate.domain.dates.service.TeamService;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.annotation.RestControllerWithMapping;
import com.fixadate.global.dto.GeneralResponseDto;
import com.fixadate.global.factory.PageFactory;
import com.fixadate.global.jwt.MemberPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestControllerWithMapping("/v1/teams")
public class TeamControllerImpl implements TeamController {
    private final TeamService teamService;

    // TODO: 4/14/25 remove under autowire if works fine without it
//    @Autowired
//    public TeamControllerImpl(TeamService teamService) {
//        this.teamService = teamService;
//    }

    @Override
    @PostMapping
    public GeneralResponseDto createTeam(@AuthenticationPrincipal final MemberPrincipal memberPrincipal,
                                            @RequestBody TeamCreateRequest request) {
        final Member member = memberPrincipal.getMember();
        Teams createdTeam = teamService.createTeam(member, request);
        return GeneralResponseDto.success("", createdTeam!=null);
    }

    @Override
    @DeleteMapping("/{id}")
    public GeneralResponseDto deleteTeam(@AuthenticationPrincipal final MemberPrincipal memberPrincipal,
                                              @PathVariable Long id) {
        final Member member = memberPrincipal.getMember();
        boolean result = teamService.deleteTeam(member, id);
        return GeneralResponseDto.success("", result);
    }

    @Override
    @GetMapping
    public GeneralResponseDto getTeams(@AuthenticationPrincipal final MemberPrincipal memberPrincipal,
        @RequestParam("page") int page,
        @RequestParam("size") int size) {
        final Member member = memberPrincipal.getMember();
        Pageable newPageable = PageFactory.getPageableSortBy(page, size, "createDate", false);
        return GeneralResponseDto.success("", teamService.getTeams(member, newPageable));
    }

    @Override
    @DeleteMapping("/members/{teamMemberId}")
    public GeneralResponseDto deleteTeamMember(@AuthenticationPrincipal final MemberPrincipal memberPrincipal,
        @PathVariable Long teamMemberId) {
        final Member member = memberPrincipal.getMember();
        boolean result = teamService.deleteTeamMember(member, teamMemberId);
        return GeneralResponseDto.success("", result);
    }
}

