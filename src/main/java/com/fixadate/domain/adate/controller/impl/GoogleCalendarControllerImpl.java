package com.fixadate.domain.adate.controller.impl;

import com.fixadate.domain.adate.controller.GoogleCalendarController;
import com.fixadate.domain.adate.dto.request.GoogleCalendarRegistRequest;
import com.fixadate.domain.adate.dto.request.GoogleCalendarTimeRequest;
import com.fixadate.domain.adate.dto.response.GoogleCalendarEventResponse;
import com.fixadate.domain.adate.service.AdateService;
import com.fixadate.global.jwt.MemberPrincipal;
import com.google.api.services.calendar.model.Channel;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/google")
@Slf4j
public class GoogleCalendarControllerImpl implements GoogleCalendarController {
    private final AdateService adateService;

    @Override
    @GetMapping()
    public ResponseEntity<List<GoogleCalendarEventResponse>> getGoogleCalendarEvents(
            @RequestParam String accessToken,
            @RequestBody GoogleCalendarTimeRequest googleCalendarTimeRequest) {
        DefaultOAuth2AccessToken oAuth2AccessToken = new DefaultOAuth2AccessToken(accessToken);
        List<GoogleCalendarEventResponse> events = adateService.listEvents(oAuth2AccessToken, googleCalendarTimeRequest);
        return ResponseEntity.ok(events);
    }

    @Override
    @PostMapping()
    public ResponseEntity<Void> registerGoogleCalendarEvents(
            @Valid @RequestBody List<GoogleCalendarRegistRequest> googleCalendarRegistRequest,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        adateService.registGoogleEvent(googleCalendarRegistRequest, memberPrincipal.getMember());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/watch")
    public ResponseEntity<Channel> watchCalendar(){
        Channel channel = adateService.executeWatchRequest();
        return ResponseEntity.ok(channel);
    }

    @PostMapping("/notifications")
    public String printNotification(
            @RequestHeader Map<String, String> headers) {
        return headers.get("x-goog-channel-id");
    }
}
