package com.fixadate.global.exception.forbidden;

import com.fixadate.global.exception.ExceptionCode;

public non-sealed class AdateUpdateForbiddenException extends ForbiddenException {

	public AdateUpdateForbiddenException(final ExceptionCode exceptionCode) {
		super(exceptionCode);
	}
}
