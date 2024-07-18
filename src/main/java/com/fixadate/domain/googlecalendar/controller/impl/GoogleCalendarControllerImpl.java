package com.fixadate.domain.googlecalendar.controller.impl;

import static com.fixadate.global.exception.ExceptionCode.*;
import static com.fixadate.global.util.constant.ConstantValue.*;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.fixadate.domain.googlecalendar.controller.GoogleCalendarController;
import com.fixadate.domain.googlecalendar.entity.constant.WebhookHeaders;
import com.fixadate.domain.googlecalendar.service.GoogleService;
import com.fixadate.domain.member.repository.MemberRepository;
import com.fixadate.global.annotation.RestControllerWithMapping;
import com.fixadate.global.exception.badrequest.GoogleIoExcetption;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.services.calendar.model.Channel;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestControllerWithMapping("/v1/google")
@RequiredArgsConstructor
public class GoogleCalendarControllerImpl implements GoogleCalendarController {
	private final GoogleService googleService;
	private final MemberRepository memberRepository;

	@Override
	@GetMapping("/watch")
	public ResponseEntity<Channel> watchCalendar(@RequestParam String userId,
		@RequestParam String memberId, HttpServletRequest request) {
		TokenResponse tokenResponse = createTokenResponse(request.getCookies());
		String accessToken = AUTHORIZATION_BEARER.getValue() + tokenResponse.getAccessToken();
		HttpHeaders headers = new HttpHeaders();
		headers.add(AUTHORIZATION.getValue(), accessToken);

		Channel channel = googleService.executeWatchRequest(userId);
		googleService.registerGoogleCredentials(channel, tokenResponse, userId, memberId);
		return ResponseEntity.status(HttpStatus.CREATED)
			.headers(headers).build();
	}

	private TokenResponse createTokenResponse(Cookie[] cookies) {
		TokenResponse tokenResponse = new TokenResponse();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(ACCESS_TOKEN.getValue())) {
				tokenResponse.setAccessToken(cookie.getValue());
			} else if (cookie.getName().equals(REFRESH_TOKEN.getValue())) {
				tokenResponse.setRefreshToken(cookie.getValue());
			}
		}
		return tokenResponse;
	}

	@Override
	@DeleteMapping()
	public ResponseEntity<Void> stopChannel(@RequestParam String memberId) {
		googleService.stopChannelAndRemoveGoogleCredentials(memberId);
		return ResponseEntity.noContent().build();
	}

	@Override
	@PostMapping("/notifications")
	public ResponseEntity<Void> getPushNotification(
		@RequestHeader(WebhookHeaders.RESOURCE_ID) String resourceId,
		@RequestHeader(WebhookHeaders.RESOURCE_URI) String resourceUri,
		@RequestHeader(WebhookHeaders.CHANNEL_ID) String channelId,
		@RequestHeader(WebhookHeaders.CHANNEL_EXPIRATION) String channelExpiration,
		@RequestHeader(WebhookHeaders.RESOURCE_STATE) String resourceState,
		@RequestHeader(WebhookHeaders.MESSAGE_NUMBER) String messageNumber) {
		try {
			googleService.listEvents(channelId);
		} catch (IOException e) {
			throw new GoogleIoExcetption(INVALID_GOOGLE_CALENDAR_REQUEST_EXECUTE);

		}
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
