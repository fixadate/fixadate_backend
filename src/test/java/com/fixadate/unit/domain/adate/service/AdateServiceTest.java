package com.fixadate.unit.domain.adate.service;

import static com.fixadate.unit.domain.adate.fixture.AdateFixture.*;
import static com.fixadate.unit.domain.colortype.fixture.ColorTypeFixture.*;
import static com.fixadate.unit.domain.member.fixture.MemberFixture.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

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
import org.springframework.transaction.annotation.Transactional;

import com.fixadate.domain.adate.dto.request.AdateRegistRequest;
import com.fixadate.domain.adate.dto.request.AdateUpdateRequest;
import com.fixadate.domain.adate.dto.response.AdateCalendarEventResponse;
import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.repository.AdateQueryRepository;
import com.fixadate.domain.adate.repository.AdateRepository;
import com.fixadate.domain.adate.service.AdateService;
import com.fixadate.domain.colortype.repository.ColorTypeRepository;
import com.fixadate.domain.member.entity.Member;

@ExtendWith(MockitoExtension.class)
@Transactional
public class AdateServiceTest {
	@InjectMocks
	private AdateService adateService;

	@Mock
	private AdateRepository adateRepository;
	@Mock
	private AdateQueryRepository adateQueryRepository;
	@Mock
	private ColorTypeRepository colorTypeRepository;

	@DisplayName("Adate를 저장한다.")
	@Test
	void registAdateEventTest() {
		AdateRegistRequest adateRegistRequest = new AdateRegistRequest(
			ADATE.getTitle(),
			ADATE.getNotes(),
			ADATE.getLocation(),
			ADATE.getAlertWhen(),
			ADATE.getRepeatFreq(),
			ADATE.getColor(),
			ADATE.getAdateName(),
			ADATE.getIfAllDay(),
			ADATE.getStartsWhen(),
			ADATE.getEndsWhen(),
			ADATE.isReminders()
		);
		given(adateRepository.save(any(Adate.class))).willReturn(ADATE);
		given(colorTypeRepository.findColorTypeByColorAndMember(any(String.class), any(Member.class))).willReturn(
			Optional.ofNullable(COLOR_TYPE));

		assertDoesNotThrow(() -> adateService.registAdateEvent(adateRegistRequest, MEMBER));
	}

	@DisplayName("Adate에 ColorType을 설정한다.")
	@Test
	void setAdateColorTypeTest() {
		given(colorTypeRepository.findColorTypeByColorAndMember(any(String.class), any(Member.class))).willReturn(
			Optional.ofNullable(COLOR_TYPE));

		adateService.setAdateColorType(ADATE, MEMBER);
		assertEquals(COLOR_TYPE, ADATE.getColorType());
	}

	@DisplayName("Adate를 삭제한다.")
	@Test
	void removeEventsTest() {
		List<Adate> adates = ADATES;

		assertDoesNotThrow(() -> adateService.removeEvents(adates));
	}

	@DisplayName("calendarId로 삭제한다.")
	@Test
	void removeAdateByCalendarIdTest() {
		given(adateRepository.findAdateByCalendarId(any(String.class))).willReturn(Optional.ofNullable(ADATE));

		assertDoesNotThrow(() -> adateService.removeAdateByCalendarId(ADATE.getCalendarId()));
	}

	@DisplayName("adate를 삭제한다.")
	@Test
	void deleteAdateTest() {
		assertDoesNotThrow(() -> adateService.removeAdate(ADATE));
	}

	@DisplayName("calendarId로 Adate를 조회한다.")
	@Test
	void getAdateByCalendarIdTest() {
		given(adateRepository.findAdateByCalendarId(ADATE.getCalendarId())).willReturn(Optional.of(ADATE));

		Optional<Adate> adate = adateService.getAdateByCalendarId(ADATE.getCalendarId());
		assertEquals(ADATE, adate.get());
	}

	@DisplayName("시간으로 Adate를 조회한다.")
	@Test
	void getAdateByStartAndEndTimeTest() {
		given(
			adateQueryRepository.findByDateRange(any(Member.class), any(LocalDateTime.class), any(LocalDateTime.class)))
			.willReturn(ADATES);

		List<AdateCalendarEventResponse> resultAdates = adateService.getAdateByStartAndEndTime(MEMBER,
			LocalDateTime.now(),
			LocalDateTime.now());

		assertEquals(ADATES.size(), resultAdates.size());
		assertEquals(ADATES.get(0).getAdateName(), resultAdates.get(0).adateName());
	}

	@DisplayName("월(month)로 Adate를 조회한다.")
	@Test
	void getAdatesByMonthTest() {
		given(
			adateQueryRepository.findByDateRange(any(Member.class), any(LocalDateTime.class), any(LocalDateTime.class)))
			.willReturn(ADATES);

		List<AdateCalendarEventResponse> resultAdates = adateService.getAdatesByMonth(2024, 5, MEMBER);

		assertEquals(ADATES.size(), resultAdates.size());
		assertEquals(ADATES.get(0).getAdateName(), resultAdates.get(0).adateName());
	}

	@DisplayName("주(week)로 Adate를 조회한다.")
	@Test
	void getAdatesByWeekTest() {
		given(
			adateQueryRepository.findByDateRange(any(Member.class), any(LocalDateTime.class), any(LocalDateTime.class)))
			.willReturn(ADATES);

		List<AdateCalendarEventResponse> resultAdates = adateService.getAdatesByWeek(LocalDate.now(), LocalDate.now(),
			MEMBER);

		assertEquals(ADATES.size(), resultAdates.size());
		assertEquals(ADATES.get(0).getAdateName(), resultAdates.get(0).adateName());
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
			ADATE.getAdateName(),
			ADATE.getIfAllDay(),
			ADATE.getStartsWhen(),
			ADATE.getEndsWhen(),
			ADATE.isReminders()
		);

		given(adateRepository.findAdateByCalendarId(any(String.class))).willReturn(Optional.ofNullable(ADATE));
		given(colorTypeRepository.findColorTypeByColorAndMember(any(String.class), any(Member.class))).willReturn(
			Optional.ofNullable(COLOR_TYPE));

		AdateCalendarEventResponse response = adateService.updateAdate("1", adateUpdateRequest, MEMBER);

		assertEquals(ADATE.getAdateName(), response.adateName());
	}
}
