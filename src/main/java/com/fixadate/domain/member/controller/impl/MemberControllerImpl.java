package com.fixadate.domain.member.controller.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.fixadate.domain.member.controller.MemberController;
import com.fixadate.domain.member.dto.request.MemberInfoUpdateRequest;
import com.fixadate.domain.member.dto.response.MemberInfoResponse;
import com.fixadate.domain.member.service.MemberService;
import com.fixadate.global.annotation.RestControllerWithMapping;

import lombok.RequiredArgsConstructor;

@RestControllerWithMapping("/v1/member")
@RequiredArgsConstructor
public class MemberControllerImpl implements MemberController {
	private final MemberService memberService;

	@Value("${randNick.adjs}")
	private String randomAdjs;
	@Value("${randNick.nouns}")
	private String randomNouns;

	@Override
	@GetMapping("/nickname")
	public ResponseEntity<String> getRandomNickname() {
		List<String> adjs = List.of(randomAdjs.split(","));
		List<String> nouns = List.of(randomNouns.split(","));

		String adj = memberService.getRandomNickname(adjs);
		String noun = memberService.getRandomNickname(nouns);
		String randomNickname = adj + " " + noun;
		return ResponseEntity.ok(randomNickname);
	}

	@Override
	@GetMapping("/{memberId}")
	public ResponseEntity<MemberInfoResponse> getMemberNickname(@PathVariable String memberId) {
		return ResponseEntity.ok(memberService.getMemberInfo(memberId));
	}

	@Override
	@PatchMapping("/{memberId}")
	public ResponseEntity<MemberInfoResponse> updateMemberNickname(@PathVariable String memberId,
		@RequestBody MemberInfoUpdateRequest memberInfoUpdateRequest) {
		return ResponseEntity.ok(memberService.updateMemberInfo(memberId, memberInfoUpdateRequest));
	}
}
