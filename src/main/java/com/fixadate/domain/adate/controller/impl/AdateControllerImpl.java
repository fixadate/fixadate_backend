package com.fixadate.domain.adate.controller.impl;

import com.fixadate.domain.adate.controller.AdateController;
import com.fixadate.domain.adate.dto.request.AdateRegistRequest;
import com.fixadate.domain.adate.dto.response.AdateCalendarEventResponse;
import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.service.AdateService;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.jwt.MemberPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/calendar")
public class AdateControllerImpl implements AdateController {
    private final AdateService adateService;

    @Override
    @PostMapping()
    public ResponseEntity<Void> registerAdateEvent(
            @Valid @RequestBody AdateRegistRequest adateRegistRequest,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        Member member = memberPrincipal.getMember();
        adateService.registAdateEvent(adateRegistRequest, member);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    @GetMapping()
    public ResponseEntity<List<AdateCalendarEventResponse>> getAdateCalendarEvents(
            @AuthenticationPrincipal MemberPrincipal memberPrincipal,
            @RequestParam LocalDateTime startDateTime,
            @RequestParam LocalDateTime endDateTime) {
        Member member = memberPrincipal.getMember();
        List<AdateCalendarEventResponse> adateCalendarEventResponses = adateService.
                getAdateCalendarEvents(member, startDateTime, endDateTime);
        return ResponseEntity.ok(adateCalendarEventResponses);
    }

    @Override
    @GetMapping("/member")
    public ResponseEntity<List<Adate>> getAdatesByMemberName(@RequestParam String memberName) {
        List<Adate> adates = adateService.getAdateResponseByMemberName(memberName);
        return ResponseEntity.ok(adates);
    }
}
