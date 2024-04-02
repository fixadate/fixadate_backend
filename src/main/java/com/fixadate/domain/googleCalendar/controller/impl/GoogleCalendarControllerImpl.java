package com.fixadate.domain.googleCalendar.controller.impl;

import com.fixadate.domain.googleCalendar.controller.GoogleCalendarController;
import com.fixadate.domain.googleCalendar.dto.response.GoogleCalendarEventResponse;
import com.fixadate.domain.googleCalendar.entity.constant.WebhookHeaders;
import com.fixadate.domain.googleCalendar.service.GoogleService;
import com.google.api.services.calendar.model.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/google")
@Slf4j
public class GoogleCalendarControllerImpl implements GoogleCalendarController {
    private final GoogleService googleService;

    @Override
    @GetMapping()
    public ResponseEntity<Void> getGoogleCalendarEvents(@RequestParam String channelId) {
        googleService.listEvents(channelId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/watch")
    public ResponseEntity<Channel> watchCalendar(@RequestParam String userId,
                                                 @RequestParam String accessToken) {
        accessToken = "Bearer " + accessToken;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", accessToken);

        Channel channel = googleService.executeWatchRequest(userId);
        googleService.registGoogleCredentials(channel, accessToken, userId);
        return ResponseEntity.ok()
                .headers(headers)
                .body(channel);
    }

    @PostMapping("/notifications")
    public ResponseEntity<List<GoogleCalendarEventResponse>> printNotification(@RequestHeader(WebhookHeaders.RESOURCE_ID) String resourceId,
                                                                               @RequestHeader(WebhookHeaders.RESOURCE_URI) String resourceUri,
                                                                               @RequestHeader(WebhookHeaders.CHANNEL_ID) String channelId,
                                                                               @RequestHeader(WebhookHeaders.CHANNEL_EXPIRATION) String channelExpiration,
                                                                               @RequestHeader(WebhookHeaders.RESOURCE_STATE) String resourceState,
                                                                               @RequestHeader(WebhookHeaders.MESSAGE_NUMBER) String messageNumber) {
        log.info("Request for calendar sync, channelId=" + channelId + ", expiration=" + channelExpiration + ", messageNumber=" + messageNumber);
        googleService.listEvents(channelId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
