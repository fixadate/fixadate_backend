package com.fixadate.domain.member.controller.impl.fixture;

import static com.fixadate.domain.member.mapper.MemberMapper.toUpdateDto;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import net.jqwik.api.Arbitraries;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fixadate.config.FixtureMonkeyConfig;
import com.fixadate.domain.member.controller.impl.MemberControllerImpl;
import com.fixadate.domain.member.dto.request.MemberInfoUpdateDto;
import com.fixadate.domain.member.dto.request.MemberInfoUpdateRequest;
import com.fixadate.domain.member.dto.response.MemberInfoDto;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.service.MemberService;
import com.fixadate.global.jwt.MemberPrincipal;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.arbitrary.CombinableArbitrary;
import com.navercorp.fixturemonkey.customizer.Values;

@SuppressWarnings("NonAsciiCharacters")
public class MemberControllerFixture {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	protected ObjectMapper objectMapper;

	@Autowired
	protected MemberControllerImpl memberController;

	@MockBean
	protected MemberService memberService;

	protected MockMvc mockMvc;
	protected String 멤버_닉네임;
	protected String 멤버_아이디;
	protected Member 멤버;
	protected MemberInfoDto 멤버_정보_응답_전달_객체;
	protected MemberInfoUpdateDto 멤버_정보_업데이트_전달_객체;
	protected MemberInfoUpdateRequest 멤버_정보_업데이트_요청;
	protected MemberPrincipal 멤버_인증_정보;

	private final FixtureMonkey dtoMonkey = FixtureMonkeyConfig.simpleValueJqwikMonkey();
	private final FixtureMonkey entityMonkey = FixtureMonkeyConfig.entityMonkey();

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
								 .apply(springSecurity())
								 .alwaysDo(print())
								 .build();

		멤버_정보_응답_전달_객체 = dtoMonkey.giveMeBuilder(MemberInfoDto.class).sample();
		멤버_정보_업데이트_요청 = dtoMonkey.giveMeBuilder(MemberInfoUpdateRequest.class)
								 .set(
									 "nickname", Values.just(CombinableArbitrary.from(
										 () -> Arbitraries.strings().alpha().ofMinLength(5).sample())
									 )
								 )
								 .sample();
		멤버_아이디 = Arbitraries.strings().alpha().ofMinLength(5).sample();
		멤버_정보_업데이트_전달_객체 = toUpdateDto(멤버_아이디, 멤버_정보_업데이트_요청);
		멤버_닉네임 = 멤버_정보_업데이트_요청.nickname();

		멤버 = entityMonkey.giveMeBuilder(Member.class)
						 .set("id", Values.just(멤버_아이디))
						 .setNotNull("name")
						 .set("role", Values.just("USER"))
						 .sample();
		멤버_인증_정보 = new MemberPrincipal(멤버);
	}
}
