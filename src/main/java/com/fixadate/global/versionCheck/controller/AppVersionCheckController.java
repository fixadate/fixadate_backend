package com.fixadate.global.versionCheck.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fixadate.global.versionCheck.dto.request.AppVersionCheckRequest;
import com.fixadate.global.versionCheck.dto.response.AppVersionCheckResponse;
import com.fixadate.global.versionCheck.service.AppVersionCheckService;

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

	@GetMapping("/v1/appVersion")
	public ResponseEntity<AppVersionCheckResponse> checkAppVersion(
		@RequestBody AppVersionCheckRequest appVersionCheckRequest) {
		return ResponseEntity.ok(appVersionCheckService.checkApiVersion(appVersionCheckRequest));
	}
}
