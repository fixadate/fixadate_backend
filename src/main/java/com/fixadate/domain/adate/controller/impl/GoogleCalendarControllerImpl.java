package com.fixadate.domain.adate.controller.impl;

import com.fixadate.domain.adate.controller.GoogleCalendarController;
import com.fixadate.domain.adate.dto.response.GoogleCalendarEventResponse;
import com.fixadate.domain.adate.entity.constant.WebhookHeaders;
import com.fixadate.domain.adate.service.AdateService;
import com.google.api.services.calendar.model.Channel;
import jakarta.servlet.http.HttpServletRequest;
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
    private final AdateService adateService;

    @Override
    @GetMapping()
    public ResponseEntity<Void> getGoogleCalendarEvents(
            HttpServletRequest httpServletRequest) {
        String userId = httpServletRequest.getSession().getId();
        adateService.listEvents(userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

//    @Override
//    @PostMapping()
//    public ResponseEntity<Void> registerGoogleCalendarEvents(
//            @Valid @RequestBody List<GoogleCalendarRegistRequest> googleCalendarRegistRequest,
//            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
//        adateService.registGoogleEvent(googleCalendarRegistRequest, memberPrincipal.getMember());
//        return ResponseEntity.status(HttpStatus.CREATED).build();
//    }

    @GetMapping("/watch")
    public ResponseEntity<Channel> watchCalendar(@RequestParam String userId,
                                                 @RequestParam String accessToken) {
        accessToken = "Bearer " + accessToken;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", accessToken);

        Channel channel = adateService.executeWatchRequest(userId);
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
                                                                               @RequestHeader(WebhookHeaders.MESSAGE_NUMBER) String messageNumber,
                                                                               HttpServletRequest request) {
        log.info("Request for calendar sync, channelId=" + channelId + ", expiration=" + channelExpiration + ", messageNumber=" + messageNumber);
        String userId = request.getSession().getId();
        adateService.listEvents(userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
