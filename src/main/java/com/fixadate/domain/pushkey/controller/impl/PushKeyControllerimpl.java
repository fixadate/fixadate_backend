package com.fixadate.domain.pushkey.controller.impl;

import com.fixadate.domain.notification.dto.FirebaseCloudMessageService;
import com.fixadate.domain.notification.enumerations.PushNotificationType;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
	private final FirebaseCloudMessageService firebaseCloudMessageService;

	@Override
	@PostMapping()
	public ResponseEntity<Void> registPushKey(@AuthenticationPrincipal MemberPrincipal memberPrincipal,
		@RequestParam String pushKey) {
		String memberId = memberPrincipal.getMemberId();
		pushKeyService.registerPushKey(pushKey, memberId);

		return ResponseEntity.noContent().build();
	}

	@Override
	@PostMapping("/test")
	public ResponseEntity<Void> testFcm(@RequestParam String pushKey) throws IOException {
		firebaseCloudMessageService.sendMessageTo(pushKey, "test제목", "test내용");

		return ResponseEntity.noContent().build();
	}

	@Override
	@PostMapping("/test-image")
	public ResponseEntity<Void> testFcmWithData(@RequestParam String pushKey, @RequestParam String image) throws IOException {
		Map<String, Object> map = new HashMap<>();
		map.put("image", image);
		map.put("link", "https://www.naver.com");
		map.put("pushType", PushNotificationType.MOVE_TO_LINK);
		firebaseCloudMessageService.sendMessageToWithData(pushKey, "test제목", "test내용", map);

		return ResponseEntity.noContent().build();
	}
}
