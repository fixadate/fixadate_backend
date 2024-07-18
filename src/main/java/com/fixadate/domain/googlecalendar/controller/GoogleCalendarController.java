package com.fixadate.domain.googlecalendar.controller;

import org.springframework.http.ResponseEntity;

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

@Tag(name = "GoogleCalendarController", description = "Google API를 관리하는 Controller입니다.")
public interface GoogleCalendarController {

	@Operation(summary = "channel 생성", description = "channel을 생성해 watch 기능을 활성화 시킵니다.")
	@Parameter(name = "userId", required = true, content = @Content(schema = @Schema(implementation = String.class)))
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "created",
			headers = @Header(name = "googleAccesstoken", description = "watch 생성을 위한 accessToken입니다."),
			content = @Content(schema = @Schema(implementation = Void.class))),
		@ApiResponse(responseCode = "400", description = "google CredentialException",
			content = @Content(schema = @Schema(implementation = Void.class)))
	})
	ResponseEntity<Channel> watchCalendar(String userId, String memberId, HttpServletRequest request);

	@Operation(summary = "push notification 수신", description = "event가 발생하면 google로부터 push notification을 수신합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "created",
			content = @Content(schema = @Schema(implementation = Void.class))),
		@ApiResponse(responseCode = "400", description = "AdateIoException",
			content = @Content(schema = @Schema(implementation = Void.class)))
	})
	ResponseEntity<Void> getPushNotification(String resourceId,
		String resourceUri,
		String channelId,
		String channelExpiration,
		String resourceState,
		String messageNumber);

	@Operation(summary = "channel 중지", description = "db에서 channel을 삭제하고 중지시킵니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "created",
			content = @Content(schema = @Schema(implementation = Void.class))),
		@ApiResponse(responseCode = "400", description = "googleIoException",
			content = @Content(schema = @Schema(implementation = Void.class)))
	})
	ResponseEntity<Void> stopChannel(String memberId);
}
