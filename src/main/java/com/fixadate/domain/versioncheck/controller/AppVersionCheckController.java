package com.fixadate.domain.versioncheck.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fixadate.domain.versioncheck.dto.request.AppVersionCheckRequest;
import com.fixadate.domain.versioncheck.dto.response.AppVersionCheckResponse;
import com.fixadate.domain.versioncheck.service.AppVersionCheckService;

import lombok.RequiredArgsConstructor;

/**
 *
 * @author yongjunhong
 * @since 2024. 7. 11.
 */
@RestController
@RequiredArgsConstructor
public class AppVersionCheckController {
	private final AppVersionCheckService appVersionCheckService;

	@PostMapping("/v1/appVersion")
	public ResponseEntity<AppVersionCheckResponse> checkAppVersion(
		@RequestBody AppVersionCheckRequest appVersionCheckRequest) {
		return ResponseEntity.ok(appVersionCheckService.checkApiVersion(appVersionCheckRequest));
	}

	@GetMapping("/v1/healthcheck")
	public ResponseEntity healthCheck() {
		return ResponseEntity.ok().build();
	}
}
