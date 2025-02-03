package com.fixadate.domain.dates.controller;

import com.fixadate.domain.dates.dto.TeamCreateRequest;
import com.fixadate.domain.dates.entity.Teams;
import com.fixadate.domain.dates.service.TeamService;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.jwt.MemberPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    @Autowired
    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping
    public ResponseEntity<Teams> deleteTeam(@AuthenticationPrincipal final MemberPrincipal memberPrincipal,
                                            @RequestBody TeamCreateRequest request) {
        final Member member = memberPrincipal.getMember();
        Teams createdTeam = teamService.createTeam(member, request);
        return new ResponseEntity<>(createdTeam, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteTeam(@AuthenticationPrincipal final MemberPrincipal memberPrincipal,
                                              @PathVariable Long id) {
        final Member member = memberPrincipal.getMember();
        boolean result = teamService.deleteTeam(member, id);
        return ResponseEntity.ok(result);
    }
}

