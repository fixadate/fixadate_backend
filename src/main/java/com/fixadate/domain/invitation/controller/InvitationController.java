package com.fixadate.domain.invitation.controller;

import com.fixadate.domain.invitation.dto.response.InvitationResponse;
import com.fixadate.domain.invitation.service.InvitationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class InvitationController {
    private final InvitationService invitationService;

    @PostMapping("/invitation/")
    public ResponseEntity<String> registInvitation(@RequestParam("teamId") Long teamId) {
        String invitationId = invitationService.registInvitation(teamId);
        return ResponseEntity.status(HttpStatus.CREATED).body(invitationId);
    }

    @GetMapping("/invitation/")
    public ResponseEntity<?> checkInvitationById(@RequestParam("id") String id) {
        return ResponseEntity.ok(invitationService.getInvitationById(id));
    }

    //중복 방지를 위해 이미 생성된 초대장이 있는지 조회하는 로직
    @GetMapping("/invitation/check/")
    public ResponseEntity<InvitationResponse> isInvitationExist(@RequestParam("teamId") Long teamId) {
        return ResponseEntity.ok(invitationService.getInvitationFromTeamId(teamId));
    }
}
