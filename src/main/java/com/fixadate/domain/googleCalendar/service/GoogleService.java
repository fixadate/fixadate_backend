package com.fixadate.domain.googleCalendar.service;

import static com.fixadate.global.exception.ExceptionCode.*;
import static com.fixadate.global.util.constant.ConstantValue.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.service.AdateService;
import com.fixadate.domain.googleCalendar.entity.GoogleCredentials;
import com.fixadate.domain.googleCalendar.repository.GoogleRepository;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.repository.MemberRepository;
import com.fixadate.global.exception.badRequest.GoogleIOExcetption;
import com.fixadate.global.exception.notFound.GoogleNotFoundException;
import com.fixadate.global.exception.notFound.MemberNotFoundException;
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
	private final AdateService adateService;
	private final GoogleRepository googleRepository;
	private final MemberRepository memberRepository;

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
			request.setTimeMin(new DateTime(oneYearAgo, TimeZone.getTimeZone("UTC")));
		}
		String pageToken = null;
		Events events = null;
		do {
			request.setPageToken(pageToken);

			try {
				events = request.execute();
			} catch (GoogleJsonResponseException e) {
				if (e.getStatusCode() == 410) {
					deleteSyncToken();
					listEvents(channelId);
				} else {
					throw new GoogleIOExcetption(INVALID_GOOGLE_CALENDAR_REQUEST_EXECUTE);
				}
			}

			List<Event> items = events.getItems();

			if (!items.isEmpty()) {
				syncEvents(items, googleCredentials.getMember());
			}
			pageToken = events.getNextPageToken();
		} while (pageToken != null);
		setNextSyncToken(events);
	}

	@Transactional
	public void syncEvents(List<Event> events, Member member) {
		List<Event> useLessEvents = new ArrayList<>();
		List<Adate> eventsToRemove = new ArrayList<>();

		for (Event event : events) {
			Optional<Adate> adateOptional = adateService.getAdateByCalendarId(event.getId());
			if (event.getStatus().equals(CALENDAR_CANCELLED.getValue())) {
				adateOptional.ifPresent(eventsToRemove::add);
				useLessEvents.add(event);
				continue;
			}
			adateOptional.ifPresent(adate -> {
				if (adate.getEtag().equals(event.getEtag())) {
					useLessEvents.add(event);
				} else {
					adate.updateFrom(event);
					useLessEvents.add(event);
				}
			});
		}

		events.removeAll(useLessEvents);
		adateService.removeEvents(eventsToRemove);
		adateService.registEvents(events, member);
	}

	public Channel executeWatchRequest(String userId) {
		return googleUtil.executeWatchRequest(userId);
	}

	public String getNextSyncToken(String userId) {
		try {
			return googleUtil.getSyncSettingsDataStore().get(SYNC_TOKEN_KEY + userId);
		} catch (IOException e) {
			throw new GoogleIOExcetption(INVALID_GOOGLE_CALENDAR_GET_SYNCTOKEN);
		}
	}

	public void setNextSyncToken(Events events) {
		try {
			googleUtil.getSyncSettingsDataStore().set(SYNC_TOKEN_KEY.getValue(), events.getNextSyncToken());
		} catch (IOException e) {
			throw new GoogleIOExcetption(INVALID_GOOGLE_CALENDAR_SET_SYNCTOKEN);
		}
	}

	public void deleteSyncToken() {
		try {
			googleUtil.getSyncSettingsDataStore().delete(SYNC_TOKEN_KEY.getValue());
		} catch (IOException e) {
			throw new GoogleIOExcetption(INVALID_GOOGLE_CALENDAR_DELETE_SYNCTOKEN);
		}
	}

	@Transactional
	public void registGoogleCredentials(Channel channel, TokenResponse tokenResponse, String userId, String
		memberId) {
		//todo : mapper 사용하는 방향으로 리팩토링 하기
		GoogleCredentials googleCredentials = GoogleCredentials
			.getGoogleCredentialsFromCredentials(channel, userId, tokenResponse);
		Member member = findMemberAndSetRelationship(memberId, googleCredentials);
		googleCredentials.setMember(member);

		googleUtil.registCredential(tokenResponse, userId);
		googleRepository.save(googleCredentials);
	}

	@Transactional
	public Member findMemberAndSetRelationship(String memberId, GoogleCredentials googleCredentials) {
		Member member = memberRepository.findMemberById(memberId).orElseThrow(() -> new MemberNotFoundException(
			NOT_FOUND_MEMBER_ID));
		googleCredentials.setMember(member);
		return member;
	}

	@Transactional
	public void stopChannelAndRemoveGoogleCredentials(String memberId) {
		Member member = memberRepository.findMemberById(memberId).orElseThrow(() -> new MemberNotFoundException(
			NOT_FOUND_MEMBER_ID));

		GoogleCredentials googleCredentials = googleRepository.findGoogleCredentialsByMember(member)
			.orElseThrow(() -> new GoogleNotFoundException(NOT_FOUND_GOOGLE_CREDENTIALS_MEMBER));

		googleUtil.stop(googleCredentials.getUserId(), googleCredentials.toChannel());
		googleCredentials.setGoogleCredentialsOrphan();
	}
}
