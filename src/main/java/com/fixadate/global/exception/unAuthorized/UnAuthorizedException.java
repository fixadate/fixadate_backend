package com.fixadate.global.exception.unAuthorized;

import com.fixadate.global.exception.ExceptionCode;

import lombok.Getter;

@Getter
public sealed class UnAuthorizedException extends RuntimeException permits AuthException, TokenException {
	private final int code;
	private final String message;

	public UnAuthorizedException(ExceptionCode exceptionCode) {
		this.code = exceptionCode.getCode();
		this.message = exceptionCode.getMessage();
	}
}
