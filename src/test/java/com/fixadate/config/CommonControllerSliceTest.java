package com.fixadate.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fixadate.domain.member.controller.impl.MemberControllerImpl;
import com.fixadate.domain.member.service.MemberService;

import lombok.Getter;

@TestComponent
@Getter
public class CommonControllerSliceTest {

	@Autowired
	protected ObjectMapper objectMapper;

	@Autowired
	protected MemberControllerImpl memberController;

	@MockBean
	protected MemberService memberService;
}
