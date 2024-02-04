package com.fixadate.domain.invitation.controller;

import com.fixadate.domain.invitation.dto.request.InvitationRequest;
import com.fixadate.domain.invitation.dto.request.InvitationSpecifyRequest;
import com.fixadate.domain.invitation.dto.response.InvitationResponse;
import com.fixadate.domain.invitation.service.InvitationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class InvitationController {
    private final InvitationService invitationService;

    @PostMapping("/invitation/")
    public ResponseEntity<Void> registInvitation(@RequestBody InvitationRequest invitationRequest) {
        invitationService.registInvitation(invitationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/invitation/") // 초대 코드를 입력해서 초대에 응답하는 로직
    public ResponseEntity<?> checkInvitationById(@RequestParam("id") String id) {
        return ResponseEntity.ok(invitationService.getInvitationById(id));
    }

    //중복 방지를 위해 이미 생성된 초대장이 있는지 조회하는 로직
    @GetMapping("/invitation/check/")
    public ResponseEntity<InvitationResponse> isInvitationExist(@RequestParam("teamId") Long datesId) {
        return ResponseEntity.ok(invitationService.getInvitationFromTeamId(datesId));
    }

    @PostMapping("/invitation/specify/")
    public ResponseEntity<?> inviteMemberToTeams(@RequestBody InvitationSpecifyRequest requestDto) {
        invitationService.inviteSpecifyMember(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/invitation/specify/")
    public ResponseEntity<?> getSpecifyInvitationByTeamId(@RequestParam("teamId") Long teamId) {
        List<InvitationResponse> responses = invitationService.getInvitationResponseFromTeamId(teamId);
        return ResponseEntity.ok(responses);
    }
}
