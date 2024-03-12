package com.fixadate.domain.adate.controller;

import com.fixadate.domain.adate.dto.request.AdateRegistRequest;
import com.fixadate.domain.adate.dto.request.GoogleCalendarRegistRequest;
import com.fixadate.domain.adate.dto.request.GoogleCalendarTimeRequest;
import com.fixadate.domain.adate.dto.response.AdateCalendarEventResponse;
import com.fixadate.domain.adate.dto.response.GoogleCalendarEventResponse;
import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.service.AdateService;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.jwt.MemberPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/calendar")
public class AdateController {
    private final AdateService adateService;

    @GetMapping("/google")
    public ResponseEntity<List<GoogleCalendarEventResponse>> getEvents(
            @RequestParam String accessToken,
            @RequestBody GoogleCalendarTimeRequest googleCalendarTimeRequest) {
        DefaultOAuth2AccessToken oAuth2AccessToken = new DefaultOAuth2AccessToken(accessToken);
        List<GoogleCalendarEventResponse> events = adateService.listEvents(oAuth2AccessToken, googleCalendarTimeRequest);
        return ResponseEntity.ok(events);
    }

    @PostMapping("/google")
    public ResponseEntity<Void> registEvents(@Valid @RequestBody List<GoogleCalendarRegistRequest> googleCalendarRegistRequest,
                                             @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        adateService.registGoogleEvent(googleCalendarRegistRequest, memberPrincipal.getMember());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping()
    public ResponseEntity<Void> registAdateEvent(@Valid @RequestBody AdateRegistRequest adateRegistRequest,
                                                 @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        Member member = memberPrincipal.getMember();
        adateService.registAdateEvent(adateRegistRequest, member);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

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

    @GetMapping("/member")
    public ResponseEntity<List<Adate>> getAdatesByMemberName(@RequestParam String memberName) {
        List<Adate> adates = adateService.getAdateResponseByMemberName(memberName);
        return ResponseEntity.ok(adates);
    }
}

