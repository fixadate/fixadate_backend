package com.fixadate.domain.pushkey.controller.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fixadate.domain.pushkey.controller.PushKeyController;
import com.fixadate.domain.pushkey.service.PushKeyService;
import com.fixadate.global.annotation.RestControllerWithMapping;
import com.fixadate.global.jwt.MemberPrincipal;

import lombok.RequiredArgsConstructor;

@RestControllerWithMapping("/v1/key")
@RequiredArgsConstructor
public class PushKeyControllerimpl implements PushKeyController {

	private final PushKeyService pushKeyService;

	@PostMapping()
	public ResponseEntity<Void> registPushKey(@AuthenticationPrincipal MemberPrincipal memberPrincipal,
		@RequestParam String pushKey) {
		String memberId = memberPrincipal.getMemberId();
		pushKeyService.registerPushKey(pushKey, memberId);

		return ResponseEntity.noContent().build();
	}
}
