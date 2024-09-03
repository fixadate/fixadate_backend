package com.fixadate.domain.googlecalendar.service;

import static com.fixadate.domain.googlecalendar.mapper.GoogleMapper.getGoogleCredentialsFromCredentials;
import static com.fixadate.domain.googlecalendar.mapper.GoogleMapper.toChannel;
import static com.fixadate.global.exception.ExceptionCode.INVALID_GOOGLE_CALENDAR_DELETE_SYNCTOKEN;
import static com.fixadate.global.exception.ExceptionCode.INVALID_GOOGLE_CALENDAR_GET_SYNCTOKEN;
import static com.fixadate.global.exception.ExceptionCode.INVALID_GOOGLE_CALENDAR_REQUEST_EXECUTE;
import static com.fixadate.global.exception.ExceptionCode.INVALID_GOOGLE_CALENDAR_SET_SYNCTOKEN;
import static com.fixadate.global.exception.ExceptionCode.NOT_FOUND_GOOGLE_CREDENTIALS_CHANNELID;
import static com.fixadate.global.util.constant.ConstantValue.CALENDAR_ID;
import static com.fixadate.global.util.constant.ConstantValue.SYNC_TOKEN_KEY;
import static com.fixadate.global.util.constant.ConstantValue.UTC;
import static com.fixadate.global.util.constant.ExternalCalendar.GOOGLE;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fixadate.domain.adate.event.object.ExternalCalendarSettingEvent;
import com.fixadate.domain.googlecalendar.entity.GoogleCredentials;
import com.fixadate.domain.googlecalendar.repository.GoogleRepository;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.exception.badrequest.GoogleIoExcetption;
import com.fixadate.global.exception.notfound.GoogleNotFoundException;
import com.fixadate.global.facade.MemberFacade;
import com.fixadate.global.util.GoogleUtil;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Channel;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleService {
	private final GoogleUtil googleUtil;
	private final GoogleRepository googleRepository;
	private final MemberFacade memberFacade;
	private final ApplicationEventPublisher applicationEventPublisher;

	@Transactional
	public void listEvents(String channelId) throws IOException {
		GoogleCredentials googleCredentials = googleRepository.findGoogleCredentialsByChannelId(channelId)
			.orElseThrow(() -> new GoogleNotFoundException(NOT_FOUND_GOOGLE_CREDENTIALS_CHANNELID));

		String userId = googleCredentials.getUserId();
		Calendar.Events.List request = googleUtil.calendarService(userId).events().list(CALENDAR_ID.getValue())
			.setOauthToken(googleCredentials.getAccessToken());
		String syncToken = getNextSyncToken(userId);

		if (syncToken != null) {
			request.setSyncToken(syncToken);
		} else {
			Date oneYearAgo = GoogleUtil.getRelativeDate(java.util.Calendar.MONTH, -1);
			request.setTimeMin(new DateTime(oneYearAgo, TimeZone.getTimeZone(UTC.getValue())));
		}
		String pageToken = null;
		Events events = null;
		do {
			request.setPageToken(pageToken);

			try {
				events = request.execute();
			} catch (GoogleJsonResponseException e) {
				if (e.getStatusCode() == 410) {
					deleteSyncToken(userId);
					listEvents(channelId);
				} else {
					throw new GoogleIoExcetption(INVALID_GOOGLE_CALENDAR_REQUEST_EXECUTE);
				}
			}

			List<Event> items = events.getItems();

			if (!items.isEmpty()) {
				syncEvents(items, googleCredentials.getMember());
			}
			pageToken = events.getNextPageToken();
		} while (pageToken != null);
		setNextSyncToken(events, userId);
	}

	@Async
	public void syncEvents(List<Event> events, Member member) {
		events.parallelStream().forEach(event ->
			applicationEventPublisher.publishEvent(new ExternalCalendarSettingEvent(event, member, GOOGLE))
		);
	}

	public Channel executeWatchRequest(String userId) {
		return googleUtil.executeWatchRequest(userId);
	}

	public String getNextSyncToken(String userId) {
		try {
			return googleUtil.getSyncSettingsDataStore().get(SYNC_TOKEN_KEY.getValue() + userId);
		} catch (IOException e) {
			throw new GoogleIoExcetption(INVALID_GOOGLE_CALENDAR_GET_SYNCTOKEN);
		}
	}

	public void setNextSyncToken(Events events, String userId) {
		try {
			googleUtil.getSyncSettingsDataStore().set(SYNC_TOKEN_KEY.getValue() + userId, events.getNextSyncToken());
		} catch (IOException e) {
			throw new GoogleIoExcetption(INVALID_GOOGLE_CALENDAR_SET_SYNCTOKEN);
		}
	}

	public void deleteSyncToken(String userId) {
		try {
			googleUtil.getSyncSettingsDataStore().delete(SYNC_TOKEN_KEY.getValue() + userId);
		} catch (IOException e) {
			throw new GoogleIoExcetption(INVALID_GOOGLE_CALENDAR_DELETE_SYNCTOKEN);
		}
	}

	@Transactional
	public void registerGoogleCredentials(Channel channel, TokenResponse tokenResponse, String userId, String
		memberId) {
		GoogleCredentials googleCredentials = getGoogleCredentialsFromCredentials(channel, userId, tokenResponse);

		memberFacade.setMemberGoogleCredentials(memberId, googleCredentials);
		googleUtil.registerCredential(tokenResponse, userId);
		googleRepository.save(googleCredentials);
	}

	@Transactional
	public void stopChannelAndRemoveGoogleCredentials(String memberId) {
		GoogleCredentials googleCredentials = memberFacade.getGoogleCredentialsByMemberId(memberId);

		googleUtil.stop(googleCredentials.getUserId(), toChannel(googleCredentials.getChannelId(), googleCredentials
			.getResourceId()));
		googleCredentials.setGoogleCredentialsOrphan();
	}
}
