package com.fixadate.domain.versioncheck.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fixadate.domain.versioncheck.dto.request.AppVersionCheckRequest;
import com.fixadate.domain.versioncheck.dto.response.AppVersionCheckResponse;
import com.fixadate.domain.versioncheck.service.AppVersionCheckService;

import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
public class AppVersionCheckController {

	private final AppVersionCheckService appVersionCheckService;

	@GetMapping("/v1/appVersion")
	public ResponseEntity<AppVersionCheckResponse> checkAppVersion(
		@RequestParam final String today,
		@RequestParam final String appVersion
	) {
		/*
		TODO : 프론트 요청에 따라 임시로 변경한 코드입니다. 추후 리팩토링 예정
		 */
		final AppVersionCheckRequest appVersionCheckRequest = new AppVersionCheckRequest(today, appVersion);

		return ResponseEntity.ok(appVersionCheckService.checkApiVersion(appVersionCheckRequest));
	}
}
