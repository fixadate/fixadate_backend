package com.fixadate.domain.dates.controller;


import com.fixadate.domain.dates.dto.DatesDto;
import com.fixadate.domain.dates.dto.DatesRegisterDto;
import com.fixadate.domain.dates.dto.DatesRegisterRequest;
import com.fixadate.domain.dates.dto.DatesResponse;
import com.fixadate.domain.dates.dto.DatesUpdateDto;
import com.fixadate.domain.dates.dto.DatesUpdateRequest;
import com.fixadate.domain.dates.dto.TeamCreateRequest;
import com.fixadate.domain.dates.entity.Teams;
import com.fixadate.domain.dates.mapper.DatesMapper;
import com.fixadate.domain.dates.service.TeamService;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.jwt.MemberPrincipal;
import jakarta.validation.Valid;
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
    public ResponseEntity<Teams> createTeam(@AuthenticationPrincipal final MemberPrincipal memberPrincipal,
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

    @PostMapping("/dates")
    public ResponseEntity<DatesResponse> createDates(@Valid @RequestBody final DatesRegisterRequest request,
        @AuthenticationPrincipal final MemberPrincipal memberPrincipal){

        final Member member = memberPrincipal.getMember();
        final DatesRegisterDto datesRegisterDto = DatesMapper.toDatesRegisterDto(request);
        DatesDto datesDto = teamService.createDates(datesRegisterDto, member);
        DatesResponse datesResponse = DatesMapper.toDatesResponse(datesDto);
        return ResponseEntity.ok(datesResponse);
    }

    @PatchMapping("/dates/{id}")
    public ResponseEntity<DatesResponse> updateDates(
        @AuthenticationPrincipal final MemberPrincipal memberPrincipal,
        @PathVariable Long id,
        @Valid @RequestBody final DatesUpdateRequest request
    ){
        final Member member = memberPrincipal.getMember();
        final DatesUpdateDto datesUpdateDto = DatesMapper.toDatesUpdateDto(request);
        DatesDto datesDto = teamService.updateDates(datesUpdateDto, id, member);
        DatesResponse datesResponse = DatesMapper.toDatesResponse(datesDto);
        return ResponseEntity.ok(datesResponse);
    }

    @DeleteMapping("/dates/{id}")
    public ResponseEntity<Boolean> deleteDates(
        @AuthenticationPrincipal final MemberPrincipal memberPrincipal,
        @PathVariable Long id){
        final Member member = memberPrincipal.getMember();
        boolean result = teamService.deleteDates(id, member);
        return ResponseEntity.ok(result);
    }
}

