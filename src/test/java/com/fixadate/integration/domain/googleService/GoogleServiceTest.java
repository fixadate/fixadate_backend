package com.fixadate.integration.domain.googleService;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.repository.AdateRepository;
import com.fixadate.domain.googleCalendar.service.GoogleService;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.repository.MemberRepository;
import com.fixadate.global.auth.dto.request.MemberRegistRequest;
import com.fixadate.global.auth.service.AuthService;
import com.fixadate.integration.config.DataClearExtension;
import com.fixadate.integration.domain.googleService.mapper.EventMapper;
import com.google.api.services.calendar.model.Event;

/**
 *
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

	static MySQLContainer mySQLContainer = new MySQLContainer<>("mysql:8.0.31");

	@BeforeAll
	static void initDataBase(@Autowired DataSource dataSource) {
		try (Connection conn = dataSource.getConnection()) {
			mySQLContainer.start();
			ScriptUtils.executeSqlScript(conn, new ClassPathResource("/sql/init/dropTable.sql"));
			ScriptUtils.executeSqlScript(conn, new ClassPathResource("/sql/init/google_credentials_test.sql"));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@DisplayName("이벤트 동기화 테스트")
	@Test
	void syncEventsTest() {
		List<Adate> adate = EventMapper.createAdates();

		MemberRegistRequest memberRegistRequest = EventMapper.createRegistDto();
		authService.registMember(memberRegistRequest);
		Optional<Member> member = memberRepository.findMemberByEmail(memberRegistRequest.email());
		List<Event> events = adate.stream().map(EventMapper::toEvent).collect(Collectors.toList());

		assertAll(
			() -> assertDoesNotThrow(() -> googleService.syncEvents(events, member.get())),
			() -> assertNotNull(member.get().getAdates())
		);
	}

}
