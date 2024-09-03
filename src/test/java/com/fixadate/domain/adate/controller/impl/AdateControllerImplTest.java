package com.fixadate.domain.adate.controller.impl;

import static com.fixadate.global.exception.ExceptionCode.FORBIDDEN_UPDATE_ADATE;
import static com.fixadate.global.exception.ExceptionCode.NOT_FOUND_ADATE_CALENDAR_ID;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fixadate.domain.adate.controller.impl.fixture.AdateControllerImplFixture;
import com.fixadate.domain.adate.service.AdateService;
import com.fixadate.global.exception.forbidden.AdateUpdateForbiddenException;
import com.fixadate.global.exception.notfound.AdateNotFoundException;

@WebMvcTest(AdateControllerImpl.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class AdateControllerImplTest extends AdateControllerImplFixture {

	@MockBean
	private AdateService adateService;

	@Autowired
	private ObjectMapper objectMapper;

	private MockMvc mockMvc;

	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

	@BeforeEach
	void setUp(@Autowired final WebApplicationContext webApplicationContext) {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
								 .apply(springSecurity())
								 .alwaysDo(print())
								 .build();
	}

	@Nested
	@DisplayName("일정 등록 테스트")
	class RegisterAdateEventTest {

		@Test
		void 일정_등록() throws Exception {
			// given
			willDoNothing().given(adateService).registerAdate(일정_저장_요청_전달_객체, 회원);

			// when & then
			mockMvc.perform(
				post("/v1/calendar")
					.with(user(회원_인증_정보))
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(일정_저장_요청))
			).andExpectAll(
				status().isOk(),
				jsonPath("$.title", is(일정_저장_요청.title())),
				jsonPath("$.notes", is(일정_저장_요청.notes())),
				jsonPath("$.location", is(일정_저장_요청.location())),
				jsonPath("$.alertWhen", startsWith(일정_저장_요청.alertWhen().format(formatter))),
				jsonPath("$.repeatFreq", startsWith(일정_저장_요청.repeatFreq().format(formatter))),
				jsonPath("$.tagName", is(일정_저장_요청.tagName())),
				jsonPath("$.ifAllDay", is(일정_저장_요청.ifAllDay())),
				jsonPath("$.startsWhen", startsWith(일정_저장_요청.startsWhen().format(formatter))),
				jsonPath("$.endsWhen", startsWith(일정_저장_요청.endsWhen().format(formatter))),
				jsonPath("$.reminders", is(일정_저장_요청.reminders()))
			);
		}

		@Test
		void 제목이_입력되지_않았다면_400을_반환한다() throws Exception {
			// when & then
			mockMvc.perform(
				post("/v1/calendar")
					.with(user(회원_인증_정보))
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(제목이_없는_일정_저장_요청))
			).andExpectAll(
				status().isBadRequest(),
				jsonPath("$.code", is(BAD_REQUEST.value())),
				jsonPath("$.message", is("Adate title cannot be blank"))
			);
		}

		@Test
		void 시작일이_입력되지_않았다면_400을_반환한다() throws Exception {
			// when & then
			mockMvc.perform(
				post("/v1/calendar")
					.with(user(회원_인증_정보))
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(시작일이_없는_일정_저장_요청))
			).andExpectAll(
				status().isBadRequest(),
				jsonPath("$.code", is(BAD_REQUEST.value())),
				jsonPath("$.message", is("Adate startsWhen cannot be null"))
			);
		}

		@Test
		void 종료일이_입력되지_않았다면_400을_반환한다() throws Exception {
			// when & then
			mockMvc.perform(
				post("/v1/calendar")
					.with(user(회원_인증_정보))
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(종료일이_없는_일정_저장_요청))
			).andExpectAll(
				status().isBadRequest(),
				jsonPath("$.code", is(BAD_REQUEST.value())),
				jsonPath("$.message", is("Adate endsWhen cannot be null"))
			);
		}
	}

	@Nested
	@DisplayName("일정 목록 조회 테스트")
	class GetAdatesTest {

		@Test
		void 시작일과_종료일에_해당하는_일정을_조회한다() throws Exception {
			// given
			given(adateService.getAdateByStartAndEndTime(회원, 시작일, 종료일)).willReturn(일정_조회_응답_전달_객체);

			// when & then
			mockMvc.perform(
				get("/v1/calendar")
					.with(user(회원_인증_정보))
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.queryParam("startDateTime", 시작일.toString())
					.queryParam("endDateTime", 종료일.toString())
			).andExpectAll(
				status().isOk(),
				jsonPath("$.[0].title", is(일정_조회_응답_전달_객체.get(0).title())),
				jsonPath("$.[0].notes", is(일정_조회_응답_전달_객체.get(0).notes())),
				jsonPath("$.[0].ifAllDay", is(일정_조회_응답_전달_객체.get(0).ifAllDay())),
				jsonPath("$.[0].startsWhen", startsWith(일정_조회_응답_전달_객체.get(0).startsWhen().format(formatter))),
				jsonPath("$.[0].endsWhen", startsWith(일정_조회_응답_전달_객체.get(0).endsWhen().format(formatter))),
				jsonPath("$.[0].calendarId", is(일정_조회_응답_전달_객체.get(0).calendarId())),
				jsonPath("$.[1].title", is(일정_조회_응답_전달_객체.get(1).title())),
				jsonPath("$.[1].notes", is(일정_조회_응답_전달_객체.get(1).notes())),
				jsonPath("$.[1].ifAllDay", is(일정_조회_응답_전달_객체.get(1).ifAllDay())),
				jsonPath("$.[1].startsWhen", startsWith(일정_조회_응답_전달_객체.get(1).startsWhen().format(formatter))),
				jsonPath("$.[1].endsWhen", startsWith(일정_조회_응답_전달_객체.get(1).endsWhen().format(formatter))),
				jsonPath("$.[1].calendarId", is(일정_조회_응답_전달_객체.get(1).calendarId()))
			);
		}

		@Test
		void 시작일을_입력하지_않은_경우_400을_반환한다() throws Exception {
			// when & then
			mockMvc.perform(
				get("/v1/calendar")
					.with(user(회원_인증_정보))
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.queryParam("endDateTime", 종료일.toString())
			).andExpectAll(
				status().isBadRequest(),
				jsonPath("$.code", is(BAD_REQUEST.value())),
				jsonPath("$.message", containsString("startDateTime"))
			);
		}

		@Test
		void 종료일을_입력하지_않은_경우_400을_반환한다() throws Exception {
			// when & then
			mockMvc.perform(
				get("/v1/calendar")
					.with(user(회원_인증_정보))
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.queryParam("startDateTime", 시작일.toString())
			).andExpectAll(
				status().isBadRequest(),
				jsonPath("$.code", is(BAD_REQUEST.value())),
				jsonPath("$.message", containsString("endDateTime"))
			);
		}
	}

	@Nested
	@DisplayName("일정 복구 테스트")
	class RestoreAdateTest {

		@Test
		void 일정을_복구한다() throws Exception {
			// given
			given(adateService.restoreAdateByCalendarId(캘린더_아이디)).willReturn(일정_정보_응답_전달_객체);

			// when & then
			mockMvc.perform(
				post("/v1/calendar/restore/{calendarId}", 캘린더_아이디)
					.with(user(회원_인증_정보))
					.with(csrf())
			).andExpectAll(
				status().isOk(),
				jsonPath("$.title", is(일정_정보_응답_전달_객체.title())),
				jsonPath("$.notes", is(일정_정보_응답_전달_객체.notes())),
				jsonPath("$.location", is(일정_정보_응답_전달_객체.location())),
				jsonPath("$.alertWhen", startsWith(일정_정보_응답_전달_객체.alertWhen().format(formatter))),
				jsonPath("$.repeatFreq", startsWith(일정_정보_응답_전달_객체.repeatFreq().format(formatter))),
				jsonPath("$.color", is(일정_정보_응답_전달_객체.color())),
				jsonPath("$.ifAllDay", is(일정_정보_응답_전달_객체.ifAllDay())),
				jsonPath("$.startsWhen", startsWith(일정_정보_응답_전달_객체.startsWhen().format(formatter))),
				jsonPath("$.endsWhen", startsWith(일정_정보_응답_전달_객체.endsWhen().format(formatter))),
				jsonPath("$.calendarId", is(일정_정보_응답_전달_객체.calendarId())),
				jsonPath("$.reminders", is(일정_정보_응답_전달_객체.reminders()))
			);
		}
	}

	@Nested
	@DisplayName("캘린더 아이디로 일정 조회 테스트")
	class GetAdateTest {

		@Test
		void 일정을_조회한다() throws Exception {
			// given
			given(adateService.getAdateInformationByCalendarId(캘린더_아이디)).willReturn(일정_정보_응답_전달_객체);

			// when & then
			mockMvc.perform(
				get("/v1/calendar/{calendarId}", 캘린더_아이디)
					.with(user(회원_인증_정보))
					.with(csrf())
			).andExpectAll(
				status().isOk(),
				jsonPath("$.title", is(일정_정보_응답_전달_객체.title())),
				jsonPath("$.notes", is(일정_정보_응답_전달_객체.notes())),
				jsonPath("$.location", is(일정_정보_응답_전달_객체.location())),
				jsonPath("$.alertWhen", startsWith(일정_정보_응답_전달_객체.alertWhen().format(formatter))),
				jsonPath("$.repeatFreq", startsWith(일정_정보_응답_전달_객체.repeatFreq().format(formatter))),
				jsonPath("$.color", is(일정_정보_응답_전달_객체.color())),
				jsonPath("$.ifAllDay", is(일정_정보_응답_전달_객체.ifAllDay())),
				jsonPath("$.startsWhen", startsWith(일정_정보_응답_전달_객체.startsWhen().format(formatter))),
				jsonPath("$.endsWhen", startsWith(일정_정보_응답_전달_객체.endsWhen().format(formatter))),
				jsonPath("$.calendarId", is(일정_정보_응답_전달_객체.calendarId())),
				jsonPath("$.reminders", is(일정_정보_응답_전달_객체.reminders()))
			);
		}

		@Test
		void 존재하지_않는_캘린더_아이디로_조회시_404를_반환한다() throws Exception {
			// given
			given(adateService.getAdateInformationByCalendarId(존재하지_않는_캘린더_아이디))
				.willThrow(new AdateNotFoundException(NOT_FOUND_ADATE_CALENDAR_ID));

			// when & then
			mockMvc.perform(
				get("/v1/calendar/{calendarId}", 존재하지_않는_캘린더_아이디)
					.with(user(회원_인증_정보))
					.with(csrf())
			).andExpectAll(
				status().isNotFound(),
				jsonPath("$.message", is(NOT_FOUND_ADATE_CALENDAR_ID.getMessage()))
			);
		}
	}

	@Nested
	@DisplayName("일정 수정 테스트")
	class UpdateAdateTest {

		@Test
		void 일정을_수정한다() throws Exception {
			// given
			given(adateService.updateAdate(회원, 캘린더_아이디, 일정_수정_요청_전달_객체)).willReturn(일정_수정_결과_응답_전달_객체);

			// when & then
			mockMvc.perform(
				patch("/v1/calendar/{calendarId}", 캘린더_아이디)
					.with(user(회원_인증_정보))
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(일정_수정_요청))
			).andExpectAll(
				status().isOk(),
				jsonPath("$.title", is(일정_수정_결과_응답_전달_객체.title())),
				jsonPath("$.notes", is(일정_수정_결과_응답_전달_객체.notes())),
				jsonPath("$.location", is(일정_수정_결과_응답_전달_객체.location())),
				jsonPath("$.alertWhen", startsWith(일정_수정_결과_응답_전달_객체.alertWhen().format(formatter))),
				jsonPath("$.repeatFreq", startsWith(일정_수정_결과_응답_전달_객체.repeatFreq().format(formatter))),
				jsonPath("$.color", is(일정_수정_결과_응답_전달_객체.color())),
				jsonPath("$.ifAllDay", is(일정_수정_결과_응답_전달_객체.ifAllDay())),
				jsonPath("$.startsWhen", startsWith(일정_수정_결과_응답_전달_객체.startsWhen().format(formatter))),
				jsonPath("$.endsWhen", startsWith(일정_수정_결과_응답_전달_객체.endsWhen().format(formatter))),
				jsonPath("$.calendarId", is(일정_수정_결과_응답_전달_객체.calendarId())),
				jsonPath("$.reminders", is(일정_수정_결과_응답_전달_객체.reminders()))
			);
		}

		@Test
		void 시작_시간_누락시_400을_반환한다() throws Exception {
			// when & then
			mockMvc.perform(
				patch("/v1/calendar/{calendarId}", 캘린더_아이디)
					.with(user(회원_인증_정보))
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(시작_시간_누락_일정_수정_요청))
			).andExpectAll(
				status().isBadRequest(),
				jsonPath("$.message").exists()
			);
		}

		@Test
		void 종료_시간_누락시_400을_반환한다() throws Exception {
			// when & then
			mockMvc.perform(
				patch("/v1/calendar/{calendarId}", 캘린더_아이디)
					.with(user(회원_인증_정보))
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(종료_시간_누락_일정_수정_요청))
			).andExpectAll(
				status().isBadRequest(),
				jsonPath("$.message").exists()
			);
		}

		@Test
		void 존재하지_않는_캘린더_아이디라면_404를_반환한다() throws Exception {
			// given
			given(adateService.updateAdate(회원, 존재하지_않는_캘린더_아이디, 일정_수정_요청_전달_객체))
				.willThrow(new AdateNotFoundException(NOT_FOUND_ADATE_CALENDAR_ID));

			// when & then
			mockMvc.perform(
				patch("/v1/calendar/{calendarId}", 존재하지_않는_캘린더_아이디)
					.with(user(회원_인증_정보))
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(일정_수정_요청))
			).andExpectAll(
				status().isNotFound(),
				jsonPath("$.message", is(NOT_FOUND_ADATE_CALENDAR_ID.getMessage()))
			);
		}

		@Test
		void 일정의_주인이_아니라면_403를_반환한다() throws Exception {
			// given
			given(adateService.updateAdate(회원, 존재하지_않는_캘린더_아이디, 일정_수정_요청_전달_객체))
				.willThrow(new AdateUpdateForbiddenException(FORBIDDEN_UPDATE_ADATE));

			// when & then
			mockMvc.perform(
				patch("/v1/calendar/{calendarId}", 존재하지_않는_캘린더_아이디)
					.with(user(회원_인증_정보))
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(일정_수정_요청))
			).andExpectAll(
				status().isForbidden(),
				jsonPath("$.message", is(FORBIDDEN_UPDATE_ADATE.getMessage()))
			);
		}
	}

	@Nested
	@DisplayName("일정 삭제 테스트")
	class RemoveAdateTest {

		@Test
		void 일정을_삭제한다() throws Exception {
			// given
			willDoNothing().given(adateService).removeAdateByCalendarId(캘린더_아이디);

			// when & then
			mockMvc.perform(
				delete("/v1/calendar/{calendarId}", 캘린더_아이디)
					.with(user(회원_인증_정보))
					.with(csrf())
			).andExpectAll(
				status().isNoContent()
			);
		}

		@Test
		void 존재하지_않는_캘린더_아이디라면_404를_반환한다() throws Exception {
			// given
			willThrow(new AdateNotFoundException(NOT_FOUND_ADATE_CALENDAR_ID))
				.given(adateService).removeAdateByCalendarId(존재하지_않는_캘린더_아이디);

			// when & then
			mockMvc.perform(
				delete("/v1/calendar/{calendarId}", 존재하지_않는_캘린더_아이디)
					.with(user(회원_인증_정보))
					.with(csrf())
			).andExpectAll(
				status().isNotFound(),
				jsonPath("$.message", is(NOT_FOUND_ADATE_CALENDAR_ID.getMessage()))
			);
		}
	}

	@Nested
	@DisplayName("월별 일정 목록 조회 테스트")
	class GetAdatesByMonthTest {

		@Test
		void 일정_목록을_조회한다() throws Exception {
			// given
			given(adateService.getAdatesByMonth(회원, 조회_연도, 조회_달)).willReturn(일정_조회_응답_전달_객체);

			// when & then
			mockMvc.perform(
				get("/v1/calendar/month")
					.with(user(회원_인증_정보))
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.queryParam("year", String.valueOf(조회_연도))
					.queryParam("month", String.valueOf(조회_달))
			).andExpectAll(
				status().isOk(),
				jsonPath("$.[0].title", is(일정_조회_응답_전달_객체.get(0).title())),
				jsonPath("$.[0].notes", is(일정_조회_응답_전달_객체.get(0).notes())),
				jsonPath("$.[0].ifAllDay", is(일정_조회_응답_전달_객체.get(0).ifAllDay())),
				jsonPath("$.[0].startsWhen", startsWith(일정_조회_응답_전달_객체.get(0).startsWhen().format(formatter))),
				jsonPath("$.[0].endsWhen", startsWith(일정_조회_응답_전달_객체.get(0).endsWhen().format(formatter))),
				jsonPath("$.[0].calendarId", is(일정_조회_응답_전달_객체.get(0).calendarId())),
				jsonPath("$.[1].title", is(일정_조회_응답_전달_객체.get(1).title())),
				jsonPath("$.[1].notes", is(일정_조회_응답_전달_객체.get(1).notes())),
				jsonPath("$.[1].ifAllDay", is(일정_조회_응답_전달_객체.get(1).ifAllDay())),
				jsonPath("$.[1].startsWhen", startsWith(일정_조회_응답_전달_객체.get(1).startsWhen().format(formatter))),
				jsonPath("$.[1].endsWhen", startsWith(일정_조회_응답_전달_객체.get(1).endsWhen().format(formatter))),
				jsonPath("$.[1].calendarId", is(일정_조회_응답_전달_객체.get(1).calendarId()))
			);
		}
	}

	@Nested
	@DisplayName("주 단위로 일정 조회 테스트")
	class GetAdatesByWeeksTest {

		@Test
		void 일정_목록을_조회한다() throws Exception {
			// given
			given(adateService.getAdatesByWeek(회원, 조회_시작_날짜, 조회_종료_날짜)).willReturn(일정_조회_응답_전달_객체);

			// when & then
			mockMvc.perform(
				get("/v1/calendar/day")
					.with(user(회원_인증_정보))
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.queryParam("firstDay", 조회_시작_날짜.toString())
					.queryParam("lastDay", 조회_종료_날짜.toString())
			).andExpectAll(
				status().isOk(),
				jsonPath("$.[0].title", is(일정_조회_응답_전달_객체.get(0).title())),
				jsonPath("$.[0].notes", is(일정_조회_응답_전달_객체.get(0).notes())),
				jsonPath("$.[0].ifAllDay", is(일정_조회_응답_전달_객체.get(0).ifAllDay())),
				jsonPath("$.[0].startsWhen", startsWith(일정_조회_응답_전달_객체.get(0).startsWhen().format(formatter))),
				jsonPath("$.[0].endsWhen", startsWith(일정_조회_응답_전달_객체.get(0).endsWhen().format(formatter))),
				jsonPath("$.[0].calendarId", is(일정_조회_응답_전달_객체.get(0).calendarId())),
				jsonPath("$.[1].title", is(일정_조회_응답_전달_객체.get(1).title())),
				jsonPath("$.[1].notes", is(일정_조회_응답_전달_객체.get(1).notes())),
				jsonPath("$.[1].ifAllDay", is(일정_조회_응답_전달_객체.get(1).ifAllDay())),
				jsonPath("$.[1].startsWhen", startsWith(일정_조회_응답_전달_객체.get(1).startsWhen().format(formatter))),
				jsonPath("$.[1].endsWhen", startsWith(일정_조회_응답_전달_객체.get(1).endsWhen().format(formatter))),
				jsonPath("$.[1].calendarId", is(일정_조회_응답_전달_객체.get(1).calendarId()))
			);
		}

		@Test
		void 조회_시작일을_전달하지_않는_경우_400을_반환한다() throws Exception {
			// when & then
			mockMvc.perform(
				get("/v1/calendar/day")
					.with(user(회원_인증_정보))
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.queryParam("lastDay", 조회_종료_날짜.toString())
			).andExpectAll(
				status().isBadRequest(),
				jsonPath("$.code", is(BAD_REQUEST.value())),
				jsonPath("$.message", containsString("firstDay"))
			);
		}

		@Test
		void 조회_종료일을_전달하지_않는_경우_400을_반환한다() throws Exception {
			// when & then
			mockMvc.perform(
				get("/v1/calendar/day")
					.with(user(회원_인증_정보))
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.queryParam("firstDay", 조회_시작_날짜.toString())
			).andExpectAll(
				status().isBadRequest(),
				jsonPath("$.code", is(BAD_REQUEST.value())),
				jsonPath("$.message", containsString("lastDay"))
			);
		}
	}
}
