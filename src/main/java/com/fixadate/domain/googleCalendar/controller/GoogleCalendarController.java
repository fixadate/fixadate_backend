package com.fixadate.domain.googleCalendar.controller;

import com.fixadate.domain.googleCalendar.dto.response.GoogleCalendarEventResponse;
import com.fixadate.domain.googleCalendar.entity.constant.WebhookHeaders;
import com.google.api.services.calendar.model.Channel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "GoogleCalendarController", description = "Google API를 관리하는 Controller입니다.")
public interface GoogleCalendarController {

    @Operation(summary = "channel 생성", description = "channel을 생성해 watch 기능을 활성화 시킵니다.", tags = {"GoogleCalendendar Controller"})
    @Parameter(name = "userId", required = true, content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "created",
                    headers = @Header(name = "googleAccesstoken", description = "watch 생성을 위한 accessToken입니다."),
                    content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "400", description = "google CredentialException",
                    content = @Content(schema = @Schema(implementation = Void.class)))
    })
    ResponseEntity<Channel> watchCalendar(@RequestParam String userId, HttpServletRequest request);

    @Operation(summary = "push notification 수신", description = "event가 발생하면 google로부터 push notification을 수신합니다.", tags = {"GoogleCalendendar Controller"})
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "created",
                    content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "400", description = "AdateIoException",
                    content = @Content(schema = @Schema(implementation = Void.class)))
    })
    ResponseEntity<List<GoogleCalendarEventResponse>> printNotification(@RequestHeader(WebhookHeaders.RESOURCE_ID) String resourceId,
                                                                        @RequestHeader(WebhookHeaders.RESOURCE_URI) String resourceUri,
                                                                        @RequestHeader(WebhookHeaders.CHANNEL_ID) String channelId,
                                                                        @RequestHeader(WebhookHeaders.CHANNEL_EXPIRATION) String channelExpiration,
                                                                        @RequestHeader(WebhookHeaders.RESOURCE_STATE) String resourceState,
                                                                        @RequestHeader(WebhookHeaders.MESSAGE_NUMBER) String messageNumber);
}
