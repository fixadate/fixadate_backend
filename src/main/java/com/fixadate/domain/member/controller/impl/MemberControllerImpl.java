package com.fixadate.domain.member.controller.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.fixadate.domain.member.controller.MemberController;
import com.fixadate.domain.member.dto.MemberInfoUpdateDto;
import com.fixadate.domain.member.dto.request.MemberInfoUpdateRequest;
import com.fixadate.domain.member.dto.response.MemberInfoResponse;
import com.fixadate.domain.member.service.MemberService;
import com.fixadate.global.annotation.RestControllerWithMapping;

import lombok.RequiredArgsConstructor;

@RestControllerWithMapping("/v1/member")
@RequiredArgsConstructor
public class MemberControllerImpl implements MemberController {

	private final MemberService memberService;

	@Override
	@GetMapping("/nickname")
	public ResponseEntity<String> getRandomNickname() {
		return ResponseEntity.ok(memberService.generateRandomNickname());
	}

	@Override
	@GetMapping("/{memberId}")
	public ResponseEntity<MemberInfoResponse> getMemberNickname(@PathVariable final String memberId) {
		return ResponseEntity.ok(memberService.getMemberInfo(memberId));
	}

	@Override
	@PatchMapping("/{memberId}")
	public ResponseEntity<MemberInfoResponse> updateMemberNickname(
		@PathVariable final String memberId,
		@RequestBody final MemberInfoUpdateRequest memberInfoUpdateRequest
	) {
		return ResponseEntity.ok(
			memberService.updateMemberInfo(MemberInfoUpdateDto.of(memberId, memberInfoUpdateRequest))
		);
	}
}
