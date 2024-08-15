package com.fixadate.domain.member.controller.impl.fixture;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import net.jqwik.api.Arbitraries;

import com.fixadate.config.CommonControllerSliceTest;
import com.fixadate.config.FixtureMonkeyConfig;
import com.fixadate.domain.member.dto.MemberInfoUpdateDto;
import com.fixadate.domain.member.dto.request.MemberInfoUpdateRequest;
import com.fixadate.domain.member.dto.response.MemberInfoResponse;
import com.fixadate.domain.member.mapper.MemberMapper;
import com.fixadate.global.exception.ControllerAdvice;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.arbitrary.CombinableArbitrary;
import com.navercorp.fixturemonkey.customizer.Values;

@SuppressWarnings("NonAsciiCharacters")
public class MemberControllerFixture extends CommonControllerSliceTest {

	protected MockMvc mockMvc;
	protected String 멤버_닉네임;
	protected String 멤버_아이디;
	protected MemberInfoResponse 멤버_정보_응답;
	protected MemberInfoUpdateDto 멤버_정보_업데이트_전달_객체;
	protected MemberInfoUpdateRequest 멤버_정보_업데이트_요청;

	private final FixtureMonkey fixtureMonkey = FixtureMonkeyConfig.simpleValueJqwikMonkey();

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(memberController)
								 .setControllerAdvice(new ControllerAdvice())
								 .alwaysDo(print())
								 .build();

		멤버_정보_응답 = fixtureMonkey.giveMeBuilder(MemberInfoResponse.class).sample();
		멤버_정보_업데이트_요청 = fixtureMonkey.giveMeBuilder(MemberInfoUpdateRequest.class)
									 .set(
										 "nickname", Values.just(CombinableArbitrary.from(
											 () -> Arbitraries.strings().alpha().ofMinLength(5).sample())
										 )
									 )
									 .sample();
		멤버_아이디 = Arbitraries.strings().alpha().ofMinLength(5).sample();
		멤버_정보_업데이트_전달_객체 = MemberMapper.toUpdateDto(멤버_아이디, 멤버_정보_업데이트_요청);
		멤버_닉네임 = 멤버_정보_업데이트_요청.nickname();
	}
}
