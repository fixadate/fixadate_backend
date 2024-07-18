package com.fixadate.domain.versioncheck.service;

import static com.fixadate.global.util.VersionComparatorUtil.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fixadate.domain.versioncheck.dto.request.AppVersionCheckRequest;
import com.fixadate.domain.versioncheck.dto.response.AppVersionCheckResponse;
import com.fixadate.domain.versioncheck.entity.UpdateType;

/**
 *
 * @author yongjunhong
 * @since 2024. 7. 11.
 */
@Service
public class AppVersionCheckService {
	@Value("${app.update.current}")
	private String currentAppVersion;

	@Value("${app.update.force}")
	private String forceUpdateVersion;

	@Value("${app.update.select}")
	private String selectUpdateVersion;

	public AppVersionCheckResponse checkApiVersion(AppVersionCheckRequest appVersionCheckRequest) {
		String appVersion = appVersionCheckRequest.appVersion();

		if (compareVersion(appVersion, forceUpdateVersion) < 0) {
			return new AppVersionCheckResponse(currentAppVersion, UpdateType.FORCE_UPDATE.getType());
		}

		if (compareVersion(appVersion, selectUpdateVersion) < 0) {
			return new AppVersionCheckResponse(currentAppVersion, UpdateType.SELECT_UPDATE.getType());
		}

		return new AppVersionCheckResponse(currentAppVersion, UpdateType.NO_UPDATE.getType());
	}
}
