package com.fixadate.domain.member.controller.impl;

import static com.fixadate.global.exception.ExceptionCode.NOT_FOUND_MEMBER_ID;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fixadate.config.CommonControllerSliceTest;
import com.fixadate.domain.member.dto.MemberInfoUpdateDto;
import com.fixadate.domain.member.dto.request.MemberInfoUpdateRequest;
import com.fixadate.domain.member.dto.response.MemberInfoResponse;
import com.fixadate.global.exception.ControllerAdvice;
import com.fixadate.global.exception.notfound.MemberNotFoundException;

@SuppressWarnings("NonAsciiCharacters")
class MemberControllerImplTest extends CommonControllerSliceTest {

	MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(memberController)
								 .setControllerAdvice(new ControllerAdvice())
								 .alwaysDo(print())
								 .build();
	}

	@Test
	void 멤버_닉네임_생성() throws Exception {
		// given
		final String nickname = "nickname";

		given(memberService.generateRandomNickname()).willReturn(nickname);

		// when & then
		mockMvc.perform(get("/v1/member/nickname").contentType("application/json"))
			   .andExpectAll(
				   status().isOk(),
				   content().string(nickname)
			   );
	}

	@DisplayName("멤버 정보 조회")
	@Nested
	class GetMemberInfoTest {

		@Test
		void 멤버_정보_조회() throws Exception {
			// given
			final String memberId = "memberId";
			final MemberInfoResponse memberInfoResponse = new MemberInfoResponse(
				"nickname",
				"profileImg",
				"signatureColor",
				"male",
				"red",
				"student",
				"2123dfsa"
			);

			given(memberService.getMemberInfo(memberId)).willReturn(memberInfoResponse);

			// when & then
			mockMvc.perform(
					   get("/v1/member/{memberId}", memberId)
						   .contentType("application/json")
				   )
				   .andExpectAll(
					   status().isOk(),
					   content().json(objectMapper.writeValueAsString(memberInfoResponse))
				   );
		}

		@Test
		void 멤버가_없는_경우_404_반환() throws Exception {
			// given
			final String memberId = "memberId";
			final MemberNotFoundException memberNotFoundException = new MemberNotFoundException(NOT_FOUND_MEMBER_ID);

			given(memberService.getMemberInfo(memberId)).willThrow(memberNotFoundException);

			// when & then
			mockMvc.perform(
					   get("/v1/member/{memberId}", memberId)
						   .contentType("application/json")
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
			final String memberId = "memberId";
			final MemberInfoUpdateRequest memberInfoUpdateRequest = new MemberInfoUpdateRequest(
				"profileImg",
				"red",
				"student",
				"image"
			);
			final MemberInfoUpdateDto memberInfoUpdateDto = new MemberInfoUpdateDto(
				memberId,
				"profileImg",
				"red",
				"student",
				"image"
			);
			final MemberInfoResponse memberInfoResponse = new MemberInfoResponse(
				"nickname",
				"profileImg",
				"signatureColor",
				"male",
				"red",
				"student",
				"2123dfsa"
			);

			given(memberService.updateMemberInfo(memberInfoUpdateDto)).willReturn(memberInfoResponse);

			// when & then
			mockMvc.perform(
					   patch("/v1/member/{memberId}", memberId)
						   .contentType("application/json")
						   .content(objectMapper.writeValueAsString(memberInfoUpdateRequest))
				   )
				   .andExpectAll(
					   status().isOk(),
					   content().json(objectMapper.writeValueAsString(memberInfoResponse))
				   );
		}

		@Test
		void 멤버가_없는_경우_404_반환() throws Exception {
			// given
			final String memberId = "memberId";
			final MemberInfoUpdateRequest memberInfoUpdateRequest = new MemberInfoUpdateRequest(
				"profileImg",
				"red",
				"student",
				"image"
			);
			final MemberInfoUpdateDto memberInfoUpdateDto = new MemberInfoUpdateDto(
				memberId,
				"profileImg",
				"red",
				"student",
				"image"
			);
			final MemberNotFoundException memberNotFoundException = new MemberNotFoundException(NOT_FOUND_MEMBER_ID);

			given(memberService.updateMemberInfo(memberInfoUpdateDto)).willThrow(memberNotFoundException);

			// when & then
			mockMvc.perform(
					   patch("/v1/member/{memberId}", memberId)
						   .contentType("application/json")
						   .content(objectMapper.writeValueAsString(memberInfoUpdateRequest))
				   )
				   .andExpectAll(
					   status().isNotFound(),
					   jsonPath("$.code", is(NOT_FOUND_MEMBER_ID.getCode())),
					   jsonPath("$.message", is(NOT_FOUND_MEMBER_ID.getMessage()))
				   );
		}
	}
}
