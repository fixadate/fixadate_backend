package com.fixadate.integration.domain.adate.service;

import static com.fixadate.global.exception.ExceptionCode.NOT_FOUND_ADATE_CALENDAR_ID;
import static com.fixadate.global.exception.ExceptionCode.NOT_FOUND_TAG_MEMBER_NAME;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import net.jqwik.api.Arbitraries;

import com.fixadate.config.DataClearExtension;
import com.fixadate.config.FixtureMonkeyConfig;
import com.fixadate.domain.adate.dto.request.AdateRegisterRequest;
import com.fixadate.domain.adate.dto.request.AdateUpdateRequest;
import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.repository.AdateRepository;
import com.fixadate.domain.adate.service.AdateService;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.repository.MemberRepository;
import com.fixadate.global.exception.ExceptionCode;
import com.fixadate.global.exception.notfound.AdateNotFoundException;
import com.fixadate.global.exception.notfound.TagNotFoundException;

import com.navercorp.fixturemonkey.FixtureMonkey;

@ExtendWith(DataClearExtension.class)
@SpringBootTest
@Testcontainers
class AdateServiceTest {

	@Autowired
	private AdateRepository adateRepository;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private AdateService adateService;
	private static final String MESSAGE = "message";

	@Container
	static MySQLContainer mySQLContainer = new MySQLContainer<>("mysql:8.0.31");

	@BeforeAll
	static void initDataBase(@Autowired DataSource dataSource) {
		try (Connection conn = dataSource.getConnection()) {
			mySQLContainer.start();
			ScriptUtils.executeSqlScript(conn, new ClassPathResource("/sql/init/dropTable.sql"));
			ScriptUtils.executeSqlScript(conn, new ClassPathResource("/sql/init/adate_test.sql"));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@AfterAll
	static void stopContainers() {
		mySQLContainer.stop();
	}

	@Nested
	@DisplayName("adate 저장 테스트")
	class AdateRegisterTest {

		@DisplayName("모든 조건이 문제 없는 경우 / 저장")
		@Sql(scripts = "/sql/setup/adate_setup.sql")
		@Test
		void registerAdateTest() {
			FixtureMonkey fixtureMonkey = FixtureMonkeyConfig.jakartaValidationMonkey();
			var adates = fixtureMonkey.giveMeBuilder(AdateRegisterRequest.class)
									  .set("tagName", Arbitraries.of("검정", "빨강", "하양", "파랑", "바이올렛"))
									  .sampleList(100);

			Optional<Member> memberOptional = memberRepository.findMemberByEmail("hong@example.com");
			assertNotNull(memberOptional.get());

			adates.forEach(adateRegistRequest -> {
				assertDoesNotThrow(
					() -> adateService.registerAdateEvent(adateRegistRequest, adateRegistRequest.tagName(),
														  memberOptional.get()
					));
			});
		}

		@DisplayName("color가 존재하지 않는 경우")
		@Sql(scripts = "/sql/setup/adate_setup.sql")
		@Test
		void registerAdateTestIfcolorNotExists() {
			Optional<Member> memberOptional = memberRepository.findMemberByEmail("hong@example.com");
			assertNotNull(memberOptional.get());

			FixtureMonkey fixtureMonkey = FixtureMonkeyConfig.jakartaValidationMonkey();
			var adateRequests = fixtureMonkey.giveMeBuilder(AdateRegisterRequest.class)
											 .set("tagName", Arbitraries.just("NU"))
											 .sampleList(100);

			adateRequests.forEach(adateRegistRequest -> {
				assertThatThrownBy(
					() -> adateService.registerAdateEvent(adateRegistRequest, adateRegistRequest.tagName(),
														  memberOptional.get()
					))
					.isInstanceOf(TagNotFoundException.class)
					.extracting(MESSAGE)
					.isEqualTo(NOT_FOUND_TAG_MEMBER_NAME.getMessage());
			});
		}
	}

	@Nested
	@DisplayName("adate 범위 조회")
	class GetAdateEventsTest {

		@DisplayName("정상 조회")
		@Sql(scripts = "/sql/setup/adate_setup.sql")
		@ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
		@CsvSource(value = {
			"'2024-04-18T10:00:00', '2024-04-20T11:00:00', hong@example.com",
			"'2024-04-20T11:00:00', '2024-04-20T11:00:00', hong@example.com",

			"'2024-04-21T09:00:00', '2024-04-23T11:00:00', hong@example.com",
			"'2024-04-21T10:00:00', '2024-04-21T11:00:00', hong@example.com",
		})
		void getAdateCalendarEventsTestWhenItsOk(LocalDateTime startsWhen, LocalDateTime endsWhen, String email) {
			Optional<Member> memberOptional = memberRepository.findMemberByEmail(email);
			assertNotNull(memberOptional.get());

			assertFalse(
				adateService.getAdateByStartAndEndTime(memberOptional.get(), startsWhen, endsWhen).isEmpty());
		}

		@DisplayName("범위에 adate가 없는 경우")
		@Sql(scripts = "/sql/setup/adate_setup.sql")
		@ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
		@CsvSource(value = {
			"'2024-04-15T09:00:00', '2024-04-14T10:00:00', hong@example.com",
			"'2024-04-17T11:00:01', '2024-04-17T12:00:00', hong@example.com",

			"'2024-04-18T09:00:00', '2024-04-18T09:59:59', hong@example.com",
			"'2024-04-18T11:00:01', '2024-04-18T12:00:00', hong@example.com",

			"'2024-04-17T11:00:01', '2024-04-18T09:00:00', hong@example.com",
			"'2023-04-16T10:00:00', '2023-04-19T11:00:00', hong@example.com",
		})
		void getAdateCalendarEventsTestWhenThereIsNoResult(
			LocalDateTime startsWhen, LocalDateTime endsWhen,
			String email
		) {
			Optional<Member> memberOptional = memberRepository.findMemberByEmail(email);

			assertTrue(
				adateService.getAdateByStartAndEndTime(memberOptional.get(), startsWhen, endsWhen).isEmpty());
		}

		@DisplayName("member가 저장한 adate가 없는 경우")
		@Sql(scripts = "/sql/setup/adate_setup.sql")
		@ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
		@CsvSource(value = {
			"'2024-04-17T09:00:00', '2024-04-17T09:59:59', hong@example.com",
			"'2024-04-17T11:00:01', '2024-04-17T12:00:00', hong@example.com",

			"'2023-04-18T09:00:00', '2023-04-18T10:00:00', hong@example.com",
			"'2024-05-18T11:00:00', '2024-05-18T12:00:00', hong@example.com",

			"'2024-04-17T12:00:00', '2024-04-18T09:00:00', hong@example.com",
			"'2024-04-16T10:00:00', '2024-04-16T11:00:00', hong@example.com",
		})
		void getAdateCalendarEventsTestWhenMemberHasNoAdate(
			LocalDateTime startsWhen, LocalDateTime endsWhen,
			String email
		) {
			Optional<Member> memberOptional = memberRepository.findMemberByEmail(email);
			assertNotNull(memberOptional.get());
			assertTrue(
				adateService.getAdateByStartAndEndTime(memberOptional.get(), startsWhen, endsWhen).isEmpty());
		}
	}

	@Nested
	@DisplayName("월 별로 adate 조회")
	class GetAdatesByMonth {
		@DisplayName("해당 월에 adate가 존재하는 경우")
		@Sql(scripts = "/sql/setup/adate_setup.sql")
		@ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
		@CsvSource(value = {
			"'2024', '4', down@example.com",
			"'2024', '5', down@example.com",
			"'2024', '6', down@example.com"
		})
		void getAdatesByMonth_Success(int year, int month, String email) {
			Optional<Member> memberOptional = memberRepository.findMemberByEmail(email);
			assertNotNull(memberOptional.get());

			assertFalse(adateService.getAdatesByMonth(year, month, memberOptional.get()).isEmpty());
		}

		@DisplayName("해당 월에 adate가 존재하지 않는 경우")
		@Sql(scripts = "/sql/setup/adate_setup.sql")
		@ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
		@CsvSource(value = {
			"'2024', '9', down@example.com",
			"'2024', '10', down@example.com",
			"'2024', '11', down@example.com"
		})
		void getAdatesByMonthWhenThereIsNoEvents(int year, int month, String email) {
			Optional<Member> memberOptional = memberRepository.findMemberByEmail(email);
			assertNotNull(memberOptional.get());

			assertEquals(0, adateService.getAdatesByMonth(year, month, memberOptional.get()).size());
		}

		@DisplayName("윤년일 때 2월달을 조회하는 경우")
		@Sql(scripts = "/sql/setup/adate_setup.sql")
		@ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
		@CsvSource(value = {
			"'2024', '2', down@example.com",
		})
		void getAdatesByMonthWhenYearIsLeapYear(int year, int month, String email) {
			Optional<Member> memberOptional = memberRepository.findMemberByEmail(email);
			assertNotNull(memberOptional.get());

			assertEquals(2, adateService.getAdatesByMonth(year, month, memberOptional.get()).size());
		}

		@DisplayName("옳지 않은 값이 들어간 경우")
		@Sql(scripts = "/sql/setup/adate_setup.sql")
		@ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
		@CsvSource(value = {
			"'2024', '-1', down@example.com",
			"'2024', '13', down@example.com"
		})
		void getAdatesByMonthWhenParameterIsInvalid(int year, int month, String email) {
			Optional<Member> memberOptional = memberRepository.findMemberByEmail(email);
			assertNotNull(memberOptional.get());

			assertAll(
				() -> assertThatThrownBy(() -> adateService.getAdatesByMonth(year, month, memberOptional.get()))
					.isInstanceOf(DateTimeException.class)
					.extracting(MESSAGE)
					.isEqualTo("Invalid value for MonthOfYear (valid values 1 - 12): " + month)
			);
		}
	}

	@Nested
	@DisplayName("주 별로 adate 조회")
	class GetAdatesByWeeks {
		@DisplayName("해당 주에 adate가 존재하는 경우")
		@Sql(scripts = "/sql/setup/adate_setup.sql")
		@ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
		@CsvSource(value = {
			"'2024-04-15', '2024-04-22', hong@example.com",
			"'2024-04-18', '2024-04-23', hong@example.com",
			"'2024-04-21', '2024-04-28', hong@example.com",
		})
		void getAdateCalendarEventsTestWhenItsOk(LocalDate startsWhen, LocalDate endsWhen, String email) {
			Optional<Member> memberOptional = memberRepository.findMemberByEmail(email);
			assertNotNull(memberOptional.get());

			assertFalse(adateService.getAdatesByWeek(startsWhen, endsWhen, memberOptional.get()).isEmpty());
		}

		@DisplayName("월이 바뀌는 경우")
		@Sql(scripts = "/sql/setup/adate_setup.sql")
		@ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
		@CsvSource(value = {
			"'2024-04-28', '2024-05-04', hong@example.com",
			"'2024-05-26', '2024-06-01', hong@example.com",
			"'2024-06-30', '2024-07-06', hong@example.com",
		})
		void getAdateCalendarEventsTestWhenMonthChange(LocalDate startsWhen, LocalDate endsWhen, String email) {
			Optional<Member> memberOptional = memberRepository.findMemberByEmail(email);
			assertNotNull(memberOptional.get());

			assertFalse(adateService.getAdatesByWeek(startsWhen, endsWhen, memberOptional.get()).isEmpty());
		}
	}

	@Nested
	@DisplayName("calendarId로 adate 조회")
	class GetAdateFromRepositoryTest {
		@DisplayName("calendarId로 조회가 가능한 경우")
		@ParameterizedTest
		@Sql(scripts = "/sql/setup/adate_setup.sql")
		@CsvSource(value = {"abc123", "def456", "ghi789", "jkl012", "mno345", "ads234", "qew267"})
		void getAdateFromRepositoryTestWhenAdateIsExists(String calendarId) {
			Optional<Adate> adateOptional = adateService.getAdateByCalendarId(calendarId);
			assertTrue(adateOptional.isPresent());
		}

		@DisplayName("calendarId로 조회가 불가능한 경우")
		@ParameterizedTest
		@Sql(scripts = "/sql/setup/adate_setup.sql")
		@CsvSource(value = {"werw123", "adsf123123", "adsfs12312", "fdsfa1232", "fdksja9i09", "e34iorjfe",
			"fjehriweq21"})
		void getAdateFromRepositoryTestWhenAdateIsNotExists(String calendarId) {
			Optional<Adate> adateOptional = adateService.getAdateByCalendarId(calendarId);
			assertFalse(adateOptional.isPresent());
		}
	}

	@Nested
	@DisplayName("adate 삭제 테스트")
	class RemoveAdateByCalendarId {
		@DisplayName("정상적으로 삭제를 한 경우")
		@ParameterizedTest
		@Sql(scripts = "/sql/setup/adate_setup.sql")
		@CsvSource(value = {"abc123", "def456", "ghi789", "jkl012", "mno345", "ads234", "qew267"})
		void removeAdateByCalendarIdWhenCalendarExists(String calendarId) {

			assertAll(
				() -> assertDoesNotThrow(() -> adateService.removeAdateByCalendarId(calendarId)),
				() -> assertTrue(adateRepository.findAdateByCalendarId(calendarId).isEmpty())
			);
		}

		@DisplayName("calendarId가 존재하지 않을 때")
		@ParameterizedTest
		@Sql(scripts = "/sql/setup/adate_setup.sql")
		@CsvSource(value = {"werw123", "adsf123123", "adsfs12312", "fdsfa1232", "fdksja9i09", "e34iorjfe",
			"fjehriweq21"})
		void removeAdateByCalendarIdWhenCalendarNotExists(String calendarId) {

			assertAll(
				() -> assertThatThrownBy(() -> adateService.removeAdateByCalendarId(calendarId))
					.isInstanceOf(AdateNotFoundException.class)
					.extracting(MESSAGE)
					.isEqualTo(NOT_FOUND_ADATE_CALENDAR_ID.getMessage()),
				() -> assertTrue(adateRepository.findAdateByCalendarId(calendarId).isEmpty())
			);
		}
	}

	@Nested
	@DisplayName("adate 수정 테스트")
	class UpdateAdate {

		@DisplayName("모든 값이 문제 없는 경우 / 수정")
		@Sql(scripts = "/sql/setup/adate_setup.sql")
		@ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
		@CsvSource(value = {
			"newTitle1, newNotes1, newLocation1, 2025-05-17T10:30:00, 2025-05-17T11:30:00, "
			+ "빨강, true, 2025-05-17T12:00:00, 2025-05-17T13:00:00, true",
			"newTitle2, newNotes2, newLocation2, 2025-05-18T10:30:00, 2025-05-18T11:30:00, "
			+ "하양, false, 2025-05-18T12:00:00, 2025-05-18T13:00:00, false",
			"newTitle3, newNotes3, newLocation3, 2025-05-19T10:30:00, 2025-05-19T11:30:00, "
			+ "파랑, true, 2025-05-19T12:00:00, 2025-05-19T13:00:00, false",
			"newTitle4, newNotes4, newLocation4, 2025-05-20T10:30:00, 2025-05-20T11:30:00, "
			+ "바이올렛, false, 2025-05-20T12:00:00, 2025-05-20T13:00:00, true",
			"newTitle5, newNotes5, newLocation5, 2025-05-21T10:30:00, 2025-05-21T11:30:00, "
			+ "검정, true, 2025-05-21T12:00:00, 2025-05-21T13:00:00, true"
		})
		void updateAdateTest_Success(
			@AggregateWith(AdateUpdateDtoAggregator.class) AdateUpdateRequest adateUpdateRequest
		) {
			Optional<Member> memberOptional = memberRepository.findMemberByEmail("hong@example.com");

			assertDoesNotThrow(
				() -> adateService.updateAdate("abc123", adateUpdateRequest, memberOptional.get()));
			Optional<Adate> adateOptional = adateService.getAdateByCalendarId("abc123");

			assertAll(
				() -> assertTrue(adateOptional.isPresent()),
				() -> assertEquals(adateUpdateRequest.title(), adateOptional.get().getTitle())
			);
		}

		@DisplayName("몇 개의 값이 제외된 경우")
		@Sql(scripts = "/sql/setup/adate_setup.sql")
		@ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
		@CsvSource(value = {
			",,, 2025-05-17T10:30:00, 2025-05-17T11:30:00, 빨강, true, 2025-05-17T12:00:00, 2025-05-17T13:00:00, true",
			",, , 2025-05-18T10:30:00, 2025-05-18T11:30:00, 하양, false,,, false",
			",,,, 2025-05-19T11:30:00, 파랑, true,, 2025-05-19T13:00:00, false",
			",,, 2025-05-20T10:30:00, 2025-05-20T11:30:00, 바이올렛, false, 2025-05-20T12:00:00, , true",
			",,,, 2025-05-21T11:30:00, 검정, true, 2025-05-21T12:00:00, 2025-05-21T13:00:00, true"
		})
		void updateAdateTestIfSomeFieldsMiss(
			@AggregateWith(AdateUpdateDtoAggregator.class) AdateUpdateRequest adateUpdateRequest
		) {
			Optional<Member> memberOptional = memberRepository.findMemberByEmail("hong@example.com");

			Optional<Adate> oldAdateOptional = adateService.getAdateByCalendarId("abc123");
			Adate oldAdate = oldAdateOptional.get();

			assertDoesNotThrow(
				() -> adateService.updateAdate("abc123", adateUpdateRequest, memberOptional.get()));
			Optional<Adate> newAdateOptional = adateService.getAdateByCalendarId("abc123");
			Adate newAdate = newAdateOptional.get();

			assertAll(
				() -> assertEquals(oldAdate.getTitle(), newAdate.getTitle()),
				() -> assertEquals(oldAdate.getNotes(), newAdate.getNotes())
			);
		}

		@DisplayName("존재하지 않는 color 값으로 수정하려고 할 때")
		@Sql(scripts = "/sql/setup/adate_setup.sql")
		@ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
		@CsvSource(value = {
			"newTitle1, newNotes1, newLocation1, 2025-05-17T10:30:00, 2025-05-17T11:30:00, "
			+ "color1, true, 2025-05-17T12:00:00, 2025-05-17T13:00:00, true",
			"newTitle2, newNotes2, newLocation2, 2025-05-18T10:30:00, 2025-05-18T11:30:00, "
			+ "color2, false, 2025-05-18T12:00:00, 2025-05-18T13:00:00, false",
			"newTitle3, newNotes3, newLocation3, 2025-05-19T10:30:00, 2025-05-19T11:30:00, "
			+ "color3, true, 2025-05-19T12:00:00, 2025-05-19T13:00:00, false",
			"newTitle4, newNotes4, newLocation4, 2025-05-20T10:30:00, 2025-05-20T11:30:00, "
			+ "color4, false, 2025-05-20T12:00:00, 2025-05-20T13:00:00, true",
			"newTitle5, newNotes5, newLocation5, 2025-05-21T10:30:00, 2025-05-21T11:30:00, "
			+ "color5, true, 2025-05-21T12:00:00, 2025-05-21T13:00:00, true"
		})
		void updateAdateTestIfColorNotExists(
			@AggregateWith(AdateUpdateDtoAggregator.class) AdateUpdateRequest adateUpdateRequest
		) {
			Optional<Member> memberOptional = memberRepository.findMemberByEmail("hong@example.com");

			assertThatThrownBy(
				() -> adateService.updateAdate("abc123", adateUpdateRequest, memberOptional.get()))
				.isInstanceOf(TagNotFoundException.class)
				.extracting(MESSAGE)
				.isEqualTo(ExceptionCode.NOT_FOUND_TAG_MEMBER_NAME.getMessage());

			Optional<Adate> adateOptional = adateService.getAdateByCalendarId("abc123");

			assertAll(
				() -> assertTrue(adateOptional.isPresent()),
				() -> assertEquals(adateUpdateRequest.title(), adateOptional.get().getTitle())
			);
		}
	}

	static class AdateUpdateDtoAggregator implements ArgumentsAggregator {
		@Override
		public Object aggregateArguments(
			ArgumentsAccessor argumentsAccessor,
			ParameterContext parameterContext
		) throws
			ArgumentsAggregationException {
			return new AdateUpdateRequest(argumentsAccessor.getString(0),
										  argumentsAccessor.getString(1),
										  argumentsAccessor.getString(2),
										  argumentsAccessor.get(3, LocalDateTime.class),
										  argumentsAccessor.get(4, LocalDateTime.class),
										  argumentsAccessor.getString(5),
										  argumentsAccessor.getBoolean(6),
										  argumentsAccessor.get(7, LocalDateTime.class),
										  argumentsAccessor.get(8, LocalDateTime.class),
										  argumentsAccessor.getBoolean(9)
			);
		}
	}
}



