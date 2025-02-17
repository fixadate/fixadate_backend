package com.fixadate.domain.member.controller.impl;

import static com.fixadate.domain.member.mapper.MemberMapper.toInfoResponse;
import static com.fixadate.domain.member.mapper.MemberMapper.toUpdateDto;

import com.fixadate.global.dto.GeneralResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.fixadate.domain.member.controller.MemberController;
import com.fixadate.domain.member.dto.request.MemberInfoUpdateDto;
import com.fixadate.domain.member.dto.request.MemberInfoUpdateRequest;
import com.fixadate.domain.member.dto.response.MemberInfoResponse;
import com.fixadate.domain.member.service.MemberService;
import com.fixadate.global.annotation.RestControllerWithMapping;
import com.fixadate.global.jwt.MemberPrincipal;

import lombok.RequiredArgsConstructor;

@RestControllerWithMapping("/v1/member")
@RequiredArgsConstructor
public class MemberControllerImpl implements MemberController {

	private final MemberService memberService;

	@Override
	@PostMapping("/nickname")
	public GeneralResponseDto getRandomNickname() {
		return GeneralResponseDto.success("", memberService.generateRandomNickname());
	}

	@Override
	@GetMapping()
	public GeneralResponseDto getMemberInfo(
		@AuthenticationPrincipal final MemberPrincipal memberPrincipal
	) {
		MemberInfoResponse response = toInfoResponse(memberService.getMemberInfo(memberPrincipal.getMemberId()));

		return GeneralResponseDto.success("", response);
	}

	@Override
	@PatchMapping()
	public GeneralResponseDto updateMemberInfo(
		@AuthenticationPrincipal final MemberPrincipal memberPrincipal,
		@RequestBody final MemberInfoUpdateRequest memberInfoUpdateRequest
	) {
		MemberInfoUpdateDto memberInfoUpdateDto = toUpdateDto(
			memberPrincipal.getMemberId(),
			memberInfoUpdateRequest
		);
		MemberInfoResponse response = toInfoResponse(memberService.updateMemberInfo(memberInfoUpdateDto));

		return GeneralResponseDto.success("", response);
	}
}
