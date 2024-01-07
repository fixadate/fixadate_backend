package com.fixadate.domain.adate.controller;

import com.fixadate.domain.adate.dto.request.GoogleCalendarTimeRequest;
import com.fixadate.domain.adate.dto.response.GoogleCalendarEventResponse;
import com.fixadate.domain.adate.service.AdateService;
import com.google.api.services.calendar.model.Event;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/googleCalendar")
    public ResponseEntity<List<GoogleCalendarEventResponse>> getEvents(@RequestParam(value = "accessToken", required = true) String token,
                                                 @RequestBody GoogleCalendarTimeRequest googleCalendarTimeRequest)
            throws IOException, GeneralSecurityException, ParseException {
        DefaultOAuth2AccessToken oAuth2AccessToken = new DefaultOAuth2AccessToken(token);
        List<GoogleCalendarEventResponse> events = adateService.listEvents(oAuth2AccessToken, googleCalendarTimeRequest);
        return ResponseEntity.ok(events);
    }
}

