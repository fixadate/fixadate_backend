package com.fixadate.unit.domain.adate.service;

import static com.fixadate.unit.domain.adate.fixture.AdateFixture.ADATE;
import static com.fixadate.unit.domain.adate.fixture.AdateFixture.ADATES;
import static com.fixadate.unit.domain.member.fixture.MemberFixture.MEMBER;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.doNothing;
import static org.mockito.BDDMockito.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import com.fixadate.domain.adate.dto.request.AdateRegisterRequest;
import com.fixadate.domain.adate.dto.request.AdateUpdateRequest;
import com.fixadate.domain.adate.dto.response.AdateResponse;
import com.fixadate.domain.adate.dto.response.AdateViewResponse;
import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.repository.AdateJpaRepository;
import com.fixadate.domain.adate.repository.AdateQueryRepository;
import com.fixadate.domain.adate.service.AdateService;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.facade.RedisFacade;

@ExtendWith(MockitoExtension.class)
@Transactional
public class AdateServiceTest {
	@InjectMocks
	private AdateService adateService;

	@Mock
	private AdateJpaRepository adateJpaRepository;
	@Mock
	private AdateQueryRepository adateQueryRepository;
	@Mock
	private RedisFacade redisFacade;
	@Mock
	private ApplicationEventPublisher applicationEventPublisher;

	@DisplayName("Adate를 저장한다.")
	@Test
	void registerAdateEventTest() {
		AdateRegisterRequest adateRegisterRequest = new AdateRegisterRequest(
			ADATE.getTitle(),
			ADATE.getNotes(),
			ADATE.getLocation(),
			ADATE.getAlertWhen(),
			ADATE.getRepeatFreq(),
			ADATE.getColor(),
			ADATE.getIfAllDay(),
			ADATE.getStartsWhen(),
			ADATE.getEndsWhen(),
			ADATE.isReminders()
		);
		given(adateJpaRepository.save(any(Adate.class))).willReturn(ADATE);

		assertDoesNotThrow(() -> adateService.registerAdateEvent(adateRegisterRequest, "ex1", MEMBER));
	}

	@DisplayName("calendarId로 Adate를 삭제하고 Redis에 저장한다.")
	@Test
	void removeAdateByCalendarIdAndSetOnRedisTest() {
		String calendarId = ADATE.getCalendarId();
		given(adateJpaRepository.findAdateByCalendarId(calendarId)).willReturn(Optional.of(ADATE));

		doNothing().when(redisFacade).removeAndRegisterObject(anyString(), any(Adate.class), any(Duration.class));

		assertDoesNotThrow(() -> adateService.removeAdateByCalendarId(calendarId));

		verify(adateJpaRepository).findAdateByCalendarId(calendarId);
		verify(adateJpaRepository).delete(ADATE);
		verify(redisFacade).removeAndRegisterObject(anyString(), eq(ADATE), any(Duration.class));
	}

	@DisplayName("calendarId로 Adate를 조회한다.")
	@Test
	void getAdateByCalendarIdTest() {
		given(adateJpaRepository.findAdateByCalendarId(ADATE.getCalendarId())).willReturn(Optional.of(ADATE));

		Optional<Adate> adate = adateService.getAdateByCalendarId(ADATE.getCalendarId());
		assertEquals(ADATE, adate.get());
	}

	@DisplayName("시간으로 Adate를 조회한다.")
	@Test
	void getAdateByStartAndEndTimeTest() {
		given(
			adateQueryRepository.findByDateRange(any(Member.class), any(LocalDateTime.class), any(LocalDateTime.class)))
			.willReturn(ADATES);

		List<AdateViewResponse> resultAdates = adateService.getAdateByStartAndEndTime(MEMBER,
			LocalDateTime.now(),
			LocalDateTime.now());

		assertEquals(ADATES.size(), resultAdates.size());
		assertEquals(ADATES.get(0).getTitle(), resultAdates.get(0).title());
	}

	@DisplayName("월(month)로 Adate를 조회한다.")
	@Test
	void getAdatesByMonthTest() {
		given(
			adateQueryRepository.findByDateRange(any(Member.class), any(LocalDateTime.class), any(LocalDateTime.class)))
			.willReturn(ADATES);

		List<AdateViewResponse> resultAdates = adateService.getAdatesByMonth(2024, 5, MEMBER);

		assertEquals(ADATES.size(), resultAdates.size());
		assertEquals(ADATES.get(0).getTitle(), resultAdates.get(0).title());
	}

	@DisplayName("주(week)로 Adate를 조회한다.")
	@Test
	void getAdatesByWeekTest() {
		given(
			adateQueryRepository.findByDateRange(any(Member.class), any(LocalDateTime.class), any(LocalDateTime.class)))
			.willReturn(ADATES);

		List<AdateViewResponse> resultAdates = adateService.getAdatesByWeek(LocalDate.now(), LocalDate.now(),
			MEMBER);

		assertEquals(ADATES.size(), resultAdates.size());
		assertEquals(ADATES.get(0).getTitle(), resultAdates.get(0).title());
	}

	@DisplayName("Adate를 수정 한다.")
	@Test
	void updateAdateTest() {
		AdateUpdateRequest adateUpdateRequest = new AdateUpdateRequest(
			ADATE.getTitle(),
			ADATE.getNotes(),
			ADATE.getLocation(),
			ADATE.getAlertWhen(),
			ADATE.getRepeatFreq(),
			ADATE.getColor(),
			ADATE.getIfAllDay(),
			ADATE.getStartsWhen(),
			ADATE.getEndsWhen(),
			ADATE.isReminders()
		);

		given(adateJpaRepository.findAdateByCalendarId(any(String.class))).willReturn(Optional.ofNullable(ADATE));

		AdateResponse response = adateService.updateAdate("1", adateUpdateRequest, MEMBER);

		assertEquals(ADATE.getTitle(), response.title());
	}
}
