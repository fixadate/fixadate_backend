package com.fixadate.integration.domain.googleservice;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fixadate.config.DataClearExtension;
import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.service.repository.AdateRepository;
import com.fixadate.domain.auth.dto.request.MemberRegisterRequest;
import com.fixadate.domain.auth.service.AuthService;
import com.fixadate.domain.googlecalendar.service.GoogleService;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.service.repository.MemberRepository;
import com.fixadate.integration.domain.googleservice.mapper.EventMapper;
import com.google.api.services.calendar.model.Event;

/**
 * @author yongjunhong
 * @since 2024. 6. 5.
 */

@ExtendWith(DataClearExtension.class)
@SpringBootTest
@Testcontainers
public class GoogleServiceTest {

	@Autowired
	private GoogleService googleService;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private AdateRepository adateRepository;
	@Autowired
	private AuthService authService;

	@DisplayName("이벤트 동기화 테스트")
	@Test
	void syncEventsTest() {
		List<Adate> adates = EventMapper.createAdates();

		MemberRegisterRequest memberRegisterRequest = EventMapper.createRegisterDto();
		authService.registerMember(memberRegisterRequest);
		Optional<Member> member = memberRepository.findMemberByEmail(memberRegisterRequest.email());
		List<Event> events = adates.stream().map(EventMapper::toEvent).collect(Collectors.toList());

		assertAll(
			() -> assertDoesNotThrow(() -> googleService.syncEvents(events, member.get())));

		for (Adate adate : adates) {
			assertNotNull(adateRepository.findAdateByCalendarId(adate.getCalendarId()));
		}
	}
}
