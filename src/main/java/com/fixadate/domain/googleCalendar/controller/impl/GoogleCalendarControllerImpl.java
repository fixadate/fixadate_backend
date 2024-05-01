package com.fixadate.domain.googleCalendar.controller.impl;

import com.fixadate.domain.googleCalendar.controller.GoogleCalendarController;
import com.fixadate.domain.googleCalendar.dto.response.GoogleCalendarEventResponse;
import com.fixadate.domain.googleCalendar.entity.constant.WebhookHeaders;
import com.fixadate.domain.googleCalendar.service.GoogleService;
import com.fixadate.global.annotation.RestControllerWithMapping;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.services.calendar.model.Channel;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static com.fixadate.global.util.constant.ConstantValue.*;


@RestControllerWithMapping("/google")
@RequiredArgsConstructor
public class GoogleCalendarControllerImpl implements GoogleCalendarController {
    private final GoogleService googleService;

    @Override
    @GetMapping("/watch")
    public ResponseEntity<Channel> watchCalendar(@RequestParam String userId,
                                                 HttpServletRequest request) {
        TokenResponse tokenResponse = createTokenResponse(request.getCookies());
        String accessToken = AUTHORIZATION_BEARER.getValue() + tokenResponse.getAccessToken();
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION.getValue(), accessToken);

        Channel channel = googleService.executeWatchRequest(userId);
        googleService.registGoogleCredentials(channel, tokenResponse, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(headers).build();
    }

    private TokenResponse createTokenResponse(Cookie[] cookies) {
        TokenResponse tokenResponse = new TokenResponse();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(ACCESS_TOKEN)) {
                tokenResponse.setAccessToken(cookie.getValue());
            } else if (cookie.getName().equals(REFRESH_TOKEN)) {
                tokenResponse.setRefreshToken(cookie.getValue());
            }
        }
        return tokenResponse;
    }

    @Override
    @PostMapping("/notifications")
    public ResponseEntity<List<GoogleCalendarEventResponse>> printNotification(@RequestHeader(WebhookHeaders.RESOURCE_ID) String resourceId,
                                                                               @RequestHeader(WebhookHeaders.RESOURCE_URI) String resourceUri,
                                                                               @RequestHeader(WebhookHeaders.CHANNEL_ID) String channelId,
                                                                               @RequestHeader(WebhookHeaders.CHANNEL_EXPIRATION) String channelExpiration,
                                                                               @RequestHeader(WebhookHeaders.RESOURCE_STATE) String resourceState,
                                                                               @RequestHeader(WebhookHeaders.MESSAGE_NUMBER) String messageNumber) {
        googleService.listEvents(channelId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
