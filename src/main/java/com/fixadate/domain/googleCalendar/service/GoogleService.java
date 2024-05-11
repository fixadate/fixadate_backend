package com.fixadate.domain.googleCalendar.service;

import static com.fixadate.global.util.constant.ConstantValue.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.exception.AdateIOException;
import com.fixadate.domain.adate.service.AdateService;
import com.fixadate.domain.googleCalendar.entity.GoogleCredentials;
import com.fixadate.domain.googleCalendar.exception.GoogleCredentialException;
import com.fixadate.domain.googleCalendar.exception.GoogleCredentialsNotFoundException;
import com.fixadate.domain.googleCalendar.repository.GoogleRepository;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.exception.MemberNotFoundException;
import com.fixadate.domain.member.repository.MemberRepository;
import com.fixadate.global.util.GoogleUtil;
import com.google.api.client.auth.oauth2.TokenResponse;
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
	public void listEvents(String channelId) {
		try {
			GoogleCredentials googleCredentials = googleRepository.findGoogleCredentialsByChannelId(channelId)
				.orElseThrow(GoogleCredentialsNotFoundException::new);

			String userId = googleCredentials.getUserId();
			Calendar.Events.List request = googleUtil.calendarService(userId).events().list(CALENDAR_ID.getValue())
				.setOauthToken(googleCredentials.getAccessToken());
			String syncToken = getNextSyncToken(userId);

			List<Event> events;
			if (syncToken == null) {
				setNextSyncToken(request.execute(), userId);
				events = request.execute().getItems();
			} else {
				request.setSyncToken(syncToken);
				setNextSyncToken(request.execute(), userId);
				events = request.execute().getItems();
			}
			syncEvents(events, googleCredentials.getMember());
		} catch (IOException e) {
			throw new AdateIOException(e);
		}
	}

	@Transactional
	public void syncEvents(List<Event> events, Member member) throws IOException {
		List<Event> useLessEvents = new ArrayList<>();
		List<Adate> eventsToRemove = new ArrayList<>();

		for (Event event : events) {
			Optional<Adate> adateOptional = adateService.getAdateFromRepository(event.getId());
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
			throw new RuntimeException(e);
		}
	}

	public void setNextSyncToken(Events events, String userId) {
		try {
			googleUtil.getSyncSettingsDataStore().set(SYNC_TOKEN_KEY + userId, events.getNextSyncToken());
		} catch (IOException e) {
			throw new GoogleCredentialException();
		}
	}

	@Transactional
	public void registGoogleCredentials(Channel channel, TokenResponse tokenResponse, String userId, String memberId) {
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
		Member member = memberRepository.findMemberById(memberId).orElseThrow(MemberNotFoundException::new);
		member.setGoogleCredentials(googleCredentials);
		return member;
	}
}
