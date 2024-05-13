package com.fixadate.global.exception.badRequest;

import com.fixadate.global.exception.ExceptionCode;

public non-sealed class OAuthPlatformBadRequest extends BadRequestException {
	public OAuthPlatformBadRequest(ExceptionCode exceptionCode) {
		super(exceptionCode);
	}
}
