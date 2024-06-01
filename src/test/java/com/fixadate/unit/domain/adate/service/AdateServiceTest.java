package com.fixadate.unit.domain.adate.service;

import static com.fixadate.unit.domain.adate.fixture.AdateFixture.*;
import static com.fixadate.unit.domain.member.fixture.MemberFixture.*;
import static com.fixadate.unit.domain.tag.fixture.TagFixture.*;
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
import com.fixadate.domain.adate.dto.response.AdateResponse;
import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.repository.AdateQueryRepository;
import com.fixadate.domain.adate.repository.AdateRepository;
import com.fixadate.domain.adate.service.AdateService;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.tag.repository.TagRepository;

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
	private TagRepository tagRepository;

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
			ADATE.getIfAllDay(),
			ADATE.getStartsWhen(),
			ADATE.getEndsWhen(),
			ADATE.isReminders()
		);
		given(adateRepository.save(any(Adate.class))).willReturn(ADATE);
		given(tagRepository.findTagByNameAndMember(any(String.class), any(Member.class))).willReturn(
			Optional.ofNullable(TAG));

		assertDoesNotThrow(() -> adateService.registAdateEvent(adateRegistRequest, "ex1", MEMBER));
	}

	@DisplayName("Adate에 Tag을 설정한다.")
	@Test
	void setAdateTagTest() {
		given(tagRepository.findTagByNameAndMember(any(String.class), any(Member.class))).willReturn(
			Optional.ofNullable(TAG));

		adateService.setAdateTag(ADATE, MEMBER, "ex1");
		assertEquals(TAG, ADATE.getTag());
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

		List<AdateResponse> resultAdates = adateService.getAdateByStartAndEndTime(MEMBER,
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

		List<AdateResponse> resultAdates = adateService.getAdatesByMonth(2024, 5, MEMBER);

		assertEquals(ADATES.size(), resultAdates.size());
		assertEquals(ADATES.get(0).getTitle(), resultAdates.get(0).title());
	}

	@DisplayName("주(week)로 Adate를 조회한다.")
	@Test
	void getAdatesByWeekTest() {
		given(
			adateQueryRepository.findByDateRange(any(Member.class), any(LocalDateTime.class), any(LocalDateTime.class)))
			.willReturn(ADATES);

		List<AdateResponse> resultAdates = adateService.getAdatesByWeek(LocalDate.now(), LocalDate.now(),
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

		given(adateRepository.findAdateByCalendarId(any(String.class))).willReturn(Optional.ofNullable(ADATE));
		given(tagRepository.findTagByNameAndMember(any(String.class), any(Member.class))).willReturn(
			Optional.ofNullable(TAG));

		AdateResponse response = adateService.updateAdate("1", adateUpdateRequest, MEMBER);

		assertEquals(ADATE.getTitle(), response.title());
	}
}
