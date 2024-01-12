package com.fixadate.domain.adate.controller;

import com.fixadate.domain.adate.dto.request.AdateRegistRequest;
import com.fixadate.domain.adate.dto.request.GoogleCalendarRegistRequest;
import com.fixadate.domain.adate.dto.request.GoogleCalendarTimeRequest;
import com.fixadate.domain.adate.dto.response.AdateCalendarEventResponse;
import com.fixadate.domain.adate.dto.response.GoogleCalendarEventResponse;
import com.fixadate.domain.adate.service.AdateService;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.jwt.MemberPrincipal;
import com.google.api.services.calendar.model.Event;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AdateController {
    private final AdateService adateService;

    @GetMapping("/calendar/google")
    public ResponseEntity<List<GoogleCalendarEventResponse>> getEvents(@RequestParam(value = "accessToken", required = true) String token,
                                                 @RequestBody GoogleCalendarTimeRequest googleCalendarTimeRequest)
            throws IOException, GeneralSecurityException, ParseException {
        DefaultOAuth2AccessToken oAuth2AccessToken = new DefaultOAuth2AccessToken(token);
        List<GoogleCalendarEventResponse> events = adateService.listEvents(oAuth2AccessToken, googleCalendarTimeRequest);
        return ResponseEntity.ok(events);
    }

    @PostMapping("/calendar/google/additional")
    public ResponseEntity<Void> registEvents(@RequestBody List<GoogleCalendarRegistRequest> googleCalendarRegistRequest,
                                          @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        adateService.registGoogleEvent(googleCalendarRegistRequest,memberPrincipal.getMember());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/calendar")
    public ResponseEntity<Void> registAdateEvent(@RequestBody AdateRegistRequest adateRegistRequest,
                                                 @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        Member member = memberPrincipal.getMember();
        adateService.registAdateEvent(adateRegistRequest, member);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/calendar")
    public ResponseEntity<List<AdateCalendarEventResponse>> getAdateCalendarEvents(
            @AuthenticationPrincipal MemberPrincipal memberPrincipal,
            @RequestParam LocalDateTime startDateTime,
            @RequestParam LocalDateTime endDateTime) {
        return ResponseEntity.
                ok()
                .build();
    }
}

