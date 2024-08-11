package com.fixadate.config;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fixadate.domain.member.controller.impl.MemberControllerImpl;
import com.fixadate.domain.member.service.MemberService;

@WebMvcTest(
	controllers = {
		MemberControllerImpl.class
	}
)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public abstract class CommonControllerSliceTest {

	@Autowired
	protected ObjectMapper objectMapper;

	@Autowired
	protected MemberControllerImpl memberController;

	@MockBean
	protected MemberService memberService;
}
