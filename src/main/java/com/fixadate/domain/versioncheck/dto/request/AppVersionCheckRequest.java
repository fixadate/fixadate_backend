package com.fixadate.domain.versioncheck.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 *
 * @author yongjunhong
 * @since 2024. 7. 11.
 */
public record AppVersionCheckRequest(
	@NotBlank(message = "Today cannot be blank")
	String today,
	@NotBlank(message = "App version cannot be blank")
	String appVersion) {
}
