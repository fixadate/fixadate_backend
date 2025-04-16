package com.fixadate.domain.versioncheck.controller;

import com.fixadate.domain.versioncheck.dto.request.AppVersionCheckRequest;
import com.fixadate.domain.versioncheck.dto.response.AppVersionCheckResponse;
import com.fixadate.global.dto.GeneralResponseDto;
import org.springframework.http.ResponseEntity;
;
import org.springframework.web.bind.annotation.RequestBody;

public interface AppVersionCheckController {
    ResponseEntity<AppVersionCheckResponse> checkAppVersion(
            @RequestBody AppVersionCheckRequest appVersionCheckRequest);

    ResponseEntity healthCheck();

    GeneralResponseDto getServerTime();
}
