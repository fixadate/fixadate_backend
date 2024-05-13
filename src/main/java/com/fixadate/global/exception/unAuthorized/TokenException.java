package com.fixadate.global.exception.unAuthorized;

import com.fixadate.global.exception.ExceptionCode;

public non-sealed class TokenException extends UnAuthorizedException {
	public TokenException(ExceptionCode exceptionCode) {
		super(exceptionCode);
	}
}
