package com.fixadate.domain.invitation.controller;

import com.fixadate.domain.invitation.dto.request.InvitationRequest;
import com.fixadate.domain.invitation.dto.response.InvitationResponse;
import com.fixadate.domain.invitation.service.InvitationService;
import com.fixadate.global.jwt.MemberPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class InvitationController {
    private final InvitationService invitationService;

    @PostMapping("/invitation/")
    public ResponseEntity<Void> registInvitation(@AuthenticationPrincipal MemberPrincipal memberPrincipal,
                                                 @RequestBody InvitationRequest invitationRequest) {
        invitationService.registInvitation(memberPrincipal.getMember(), invitationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/invitation/")
    public ResponseEntity<InvitationResponse> getInvitationResponse(@RequestParam(value = "id") String id) {
        return ResponseEntity.ok(invitationService.getInvitationFromId(id));
    }
}
