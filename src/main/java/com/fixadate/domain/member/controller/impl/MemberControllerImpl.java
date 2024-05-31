package com.fixadate.domain.member.controller.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fixadate.domain.member.controller.MemberController;
import com.fixadate.domain.member.service.MemberService;
import com.fixadate.global.annotation.RestControllerWithMapping;
import com.fixadate.global.util.S3Util;

import lombok.RequiredArgsConstructor;

@RestControllerWithMapping("/v1/member")
@RequiredArgsConstructor
public class MemberControllerImpl implements MemberController {
	private final MemberService memberService;
	private final S3Util s3Util;

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
	@GetMapping("/profile-img")
	public ResponseEntity<String> getProfileImagePresignedUrl(@RequestParam String filename) {
		return ResponseEntity.ok(s3Util.generatePresignedUrlForDownload(filename));
	}

	@Override
	@DeleteMapping("/profile-img")
	public ResponseEntity<String> getProfileImageDeletePresignedUrl(@RequestParam String filename) {
		return ResponseEntity.ok(s3Util.generatePresignedUrlForDelete(filename));
	}
}
