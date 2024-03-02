package com.fixadate.domain.adate.controller;

import com.fixadate.domain.adate.dto.request.AdateRegistRequest;
import com.fixadate.domain.adate.dto.request.GoogleCalendarRegistRequest;
import com.fixadate.domain.adate.dto.request.GoogleCalendarTimeRequest;
import com.fixadate.domain.adate.dto.response.AdateCalendarEventResponse;
import com.fixadate.domain.adate.dto.response.GoogleCalendarEventResponse;
import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.exception.GoogleAccessTokenExpiredException;
import com.fixadate.domain.adate.service.AdateService;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.jwt.MemberPrincipal;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/calendar")
public class AdateController {
    private final AdateService adateService;

    @GetMapping("/google")
    public ResponseEntity<List<GoogleCalendarEventResponse>> getEvents(
            @RequestParam String accessToken,
            @RequestBody GoogleCalendarTimeRequest googleCalendarTimeRequest)
            throws IOException, GeneralSecurityException, ParseException {
        try {
            DefaultOAuth2AccessToken oAuth2AccessToken = new DefaultOAuth2AccessToken(accessToken);
            List<GoogleCalendarEventResponse> events = adateService.listEvents(oAuth2AccessToken, googleCalendarTimeRequest);
            return ResponseEntity.ok(events);
        } catch (GoogleJsonResponseException e) {
            throw new GoogleAccessTokenExpiredException();
        }
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

