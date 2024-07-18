package com.fixadate.global.exception.unauthorized;

import com.fixadate.global.exception.ExceptionCode;

public non-sealed class AuthException extends UnAuthorizedException {
	public AuthException(ExceptionCode exceptionCode) {
		super(exceptionCode);
	}
}
