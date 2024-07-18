package com.fixadate.global.exception.badrequest;

import com.fixadate.global.exception.ExceptionCode;

public non-sealed class OAuthPlatformBadRequest extends BadRequestException {
	public OAuthPlatformBadRequest(ExceptionCode exceptionCode) {
		super(exceptionCode);
	}
}
