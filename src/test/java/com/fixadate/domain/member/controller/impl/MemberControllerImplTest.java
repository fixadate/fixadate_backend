package com.fixadate.domain.member.controller.impl;

import static com.fixadate.global.exception.ExceptionCode.NOT_FOUND_MEMBER_ID;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import com.fixadate.domain.member.controller.impl.fixture.MemberControllerFixture;
import com.fixadate.global.exception.notfound.MemberNotFoundException;

@WebMvcTest(MemberControllerImpl.class)
@SuppressWarnings("NonAsciiCharacters")
class MemberControllerImplTest extends MemberControllerFixture {

	@Test
	void 멤버_닉네임_생성() throws Exception {
		// given
		given(commonControllerSliceTest.getMemberService().generateRandomNickname()).willReturn(멤버_닉네임);

		// when & then
		mockMvc.perform(
				   post("/v1/member/nickname")
					   .contentType(APPLICATION_JSON_VALUE)
					   .with(user(멤버_인증_정보))
					   .with(csrf())
			   )
			   .andExpectAll(
				   status().isOk(),
				   content().string(멤버_닉네임)
			   );
	}

	@DisplayName("멤버 정보 조회")
	@Nested
	class GetMemberInfoTest {

		@Test
		void 멤버_정보_조회() throws Exception {
			// given
			given(commonControllerSliceTest.getMemberService().getMemberInfo(멤버_아이디)).willReturn(멤버_정보_응답_전달_객체);

			// when & then
			mockMvc.perform(
					   get("/v1/member")
						   .contentType(APPLICATION_JSON_VALUE)
						   .with(user(멤버_인증_정보))
				   )
				   .andExpectAll(
					   status().isOk(),
					   content().json(commonControllerSliceTest.getObjectMapper().writeValueAsString(멤버_정보_응답))
				   );
		}

		@Test
		void 멤버가_없는_경우_404_반환() throws Exception {
			// given
			final MemberNotFoundException memberNotFoundException = new MemberNotFoundException(NOT_FOUND_MEMBER_ID);

			given(commonControllerSliceTest.getMemberService()
										   .getMemberInfo(멤버_아이디)).willThrow(memberNotFoundException);

			// when & then
			mockMvc.perform(
					   get("/v1/member")
						   .contentType(APPLICATION_JSON_VALUE)
						   .with(user(멤버_인증_정보))
				   )
				   .andExpectAll(
					   status().isNotFound(),
					   jsonPath("$.code", is(NOT_FOUND_MEMBER_ID.getCode())),
					   jsonPath("$.message", is(NOT_FOUND_MEMBER_ID.getMessage()))
				   );
		}
	}

	@DisplayName("멤버 정보 수정")
	@Nested
	class UpdateMemberInfoTest {

		@Test
		void 멤버_정보_수정() throws Exception {
			// given
			given(commonControllerSliceTest.getMemberService().updateMemberInfo(멤버_정보_업데이트_전달_객체)).willReturn(
				멤버_정보_응답_전달_객체);

			// when & then
			mockMvc.perform(
					   patch("/v1/member")
						   .with(user(멤버_인증_정보))
						   .with(csrf())
						   .contentType(APPLICATION_JSON_VALUE)
						   .content(commonControllerSliceTest.getObjectMapper().writeValueAsString(멤버_정보_업데이트_요청))
				   )
				   .andExpectAll(
					   status().isOk(),
					   content().json(commonControllerSliceTest.getObjectMapper().writeValueAsString(멤버_정보_응답))
				   );
		}

		@Test
		void 멤버가_없는_경우_404_반환() throws Exception {
			// given
			final MemberNotFoundException memberNotFoundException = new MemberNotFoundException(NOT_FOUND_MEMBER_ID);

			given(commonControllerSliceTest.getMemberService().updateMemberInfo(멤버_정보_업데이트_전달_객체)).willThrow(
				memberNotFoundException);

			// when & then
			mockMvc.perform(
					   patch("/v1/member")
						   .with(user(멤버_인증_정보))
						   .with(csrf())
						   .contentType(APPLICATION_JSON_VALUE)
						   .content(commonControllerSliceTest.getObjectMapper().writeValueAsString(멤버_정보_업데이트_요청))
				   )
				   .andExpectAll(
					   status().isNotFound(),
					   jsonPath("$.code", is(NOT_FOUND_MEMBER_ID.getCode())),
					   jsonPath("$.message", is(NOT_FOUND_MEMBER_ID.getMessage()))
				   );
		}
	}
}
