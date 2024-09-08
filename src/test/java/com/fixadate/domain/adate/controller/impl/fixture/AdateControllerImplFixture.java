package com.fixadate.domain.adate.controller.impl.fixture;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;

import net.jqwik.api.Arbitraries;

import com.fixadate.config.FixtureMonkeyConfig;
import com.fixadate.domain.adate.dto.AdateDto;
import com.fixadate.domain.adate.dto.AdateRegisterDto;
import com.fixadate.domain.adate.dto.AdateUpdateDto;
import com.fixadate.domain.adate.dto.request.AdateRegisterRequest;
import com.fixadate.domain.adate.dto.request.AdateUpdateRequest;
import com.fixadate.domain.adate.mapper.AdateMapper;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.jwt.MemberPrincipal;

import com.navercorp.fixturemonkey.FixtureMonkey;

@SuppressWarnings("NonAsciiCharacters")
public class AdateControllerImplFixture {

	private final FixtureMonkey recordFixtureMonkey = FixtureMonkeyConfig.simpleValueJqwikMonkey();
	private final FixtureMonkey entityFixtureMonkey = FixtureMonkeyConfig.entityMonkey();

	protected Member 회원;
	protected MemberPrincipal 회원_인증_정보;
	protected AdateRegisterRequest 일정_저장_요청;
	protected AdateRegisterRequest 일정_저장_응답;
	protected AdateRegisterRequest 제목이_없는_일정_저장_요청;
	protected AdateRegisterRequest 시작일이_없는_일정_저장_요청;
	protected AdateRegisterRequest 종료일이_없는_일정_저장_요청;
	protected AdateRegisterDto 일정_저장_요청_전달_객체;
	protected LocalDateTime 시작일 = LocalDateTime.now();
	protected LocalDateTime 종료일 = 시작일.plusDays(5);
	protected List<AdateDto> 일정_조회_응답_전달_객체;
	protected AdateDto 일정_정보_저장_응답_전달_객체;
	protected AdateDto 일정_정보_응답_전달_객체;
	protected String 캘린더_아이디;
	protected String 존재하지_않는_캘린더_아이디 = "none calendar id";
	protected AdateUpdateRequest 일정_수정_요청;
	protected AdateUpdateDto 일정_수정_요청_전달_객체;
	protected AdateDto 일정_수정_결과_응답_전달_객체;
	protected AdateUpdateRequest 시작_시간_누락_일정_수정_요청;
	protected AdateUpdateRequest 종료_시간_누락_일정_수정_요청;
	protected int 조회_연도;
	protected int 조회_달;
	protected LocalDate 조회_시작_날짜 = 시작일.toLocalDate();
	protected LocalDate 조회_종료_날짜 = 종료일.toLocalDate();

	@BeforeEach
	void setUpFixture() {
		회원 = entityFixtureMonkey.giveMeBuilder(Member.class)
								.set("id", "1")
								.setNotNull("name")
								.set("role", "USER")
								.sample();
		회원_인증_정보 = new MemberPrincipal(회원);

		일정_저장_요청 = recordFixtureMonkey.giveMeBuilder(AdateRegisterRequest.class)
									  .setNotNull("title")
									  .setNotNull("alertWhen")
									  .setNotNull("repeatFreq")
									  .setNotNull("startsWhen")
									  .setNotNull("endsWhen")
									  .sample();
		일정_정보_저장_응답_전달_객체 = recordFixtureMonkey.giveMeBuilder(AdateDto.class)
											   .set("title", 일정_저장_요청.title())
											   .set("notes", 일정_저장_요청.notes())
											   .set("location", 일정_저장_요청.location())
											   .set("alertWhen", 일정_저장_요청.alertWhen())
											   .set("repeatFreq", 일정_저장_요청.repeatFreq())
											   .setNotNull("color")
											   .set("ifAllDay", 일정_저장_요청.ifAllDay())
											   .set("startsWhen", 일정_저장_요청.startsWhen())
											   .set("endsWhen", 일정_저장_요청.endsWhen())
											   .setNull("calendarId")
											   .set("reminders", 일정_저장_요청.reminders())
											   .sample();
		일정_저장_응답 = 일정_저장_요청;
		제목이_없는_일정_저장_요청 = recordFixtureMonkey.giveMeBuilder(AdateRegisterRequest.class)
											 .setNull("title")
											 .setNotNull("startsWhen")
											 .setNotNull("endsWhen")
											 .sample();
		시작일이_없는_일정_저장_요청 = recordFixtureMonkey.giveMeBuilder(AdateRegisterRequest.class)
											  .setNotNull("title")
											  .setNull("startsWhen")
											  .setNotNull("endsWhen")
											  .sample();
		종료일이_없는_일정_저장_요청 = recordFixtureMonkey.giveMeBuilder(AdateRegisterRequest.class)
											  .setNotNull("title")
											  .setNotNull("startsWhen")
											  .setNull("endsWhen")
											  .sample();
		일정_저장_요청_전달_객체 = AdateMapper.toAdateRegisterDto(일정_저장_요청);

		일정_조회_응답_전달_객체 = createAdateDto(2);

		일정_정보_응답_전달_객체 = recordFixtureMonkey.giveMeBuilder(AdateDto.class)
											.setNotNull("alertWhen")
											.setNotNull("repeatFreq")
											.setNotNull("ifAllDay")
											.setNotNull("startsWhen")
											.setNotNull("endsWhen")
											.setNotNull("calendarId")
											.setNotNull("reminders")
											.sample();
		캘린더_아이디 = 일정_정보_응답_전달_객체.calendarId();

		일정_수정_요청 = recordFixtureMonkey.giveMeBuilder(AdateUpdateRequest.class)
									  .setNotNull("ifAllDay")
									  .setNotNull("startsWhen")
									  .setNotNull("endsWhen")
									  .setNotNull("reminders")
									  .sample();
		일정_수정_요청_전달_객체 = AdateMapper.toAdateUpdateDto(일정_수정_요청);
		일정_수정_결과_응답_전달_객체 = 일정_정보_응답_전달_객체;

		시작_시간_누락_일정_수정_요청 = recordFixtureMonkey.giveMeBuilder(AdateUpdateRequest.class)
											   .setNotNull("ifAllDay")
											   .setNull("startsWhen")
											   .setNotNull("endsWhen")
											   .setNotNull("reminders")
											   .sample();
		종료_시간_누락_일정_수정_요청 = recordFixtureMonkey.giveMeBuilder(AdateUpdateRequest.class)
											   .setNotNull("ifAllDay")
											   .setNotNull("startsWhen")
											   .setNull("endsWhen")
											   .setNotNull("reminders")
											   .sample();
		조회_연도 = Arbitraries.integers().between(2000, 2060).sample();
		조회_달 = Arbitraries.integers().between(1, 12).sample();
	}

	private List<AdateDto> createAdateDto(final int num) {
		final List<AdateDto> adateDtos = new ArrayList<>(num);

		for (int i = 0; i < num; i++) {
			final AdateDto adateDto = recordFixtureMonkey.giveMeBuilder(AdateDto.class)
														 .setNotNull("ifAllDay")
														 .set("startsWhen", 시작일)
														 .set("endsWhen", 종료일)
														 .sample();
			adateDtos.add(adateDto);
		}

		return adateDtos;
	}
}
