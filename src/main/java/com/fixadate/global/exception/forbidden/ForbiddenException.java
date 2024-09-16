package com.fixadate.global.exception.forbidden;

import com.fixadate.global.exception.ExceptionCode;

import lombok.Getter;

@Getter
public sealed class ForbiddenException extends RuntimeException
	permits AdateUpdateForbiddenException {

	private final int code;
	private final String message;

	public ForbiddenException(final ExceptionCode exceptionCode) {
		this.code = exceptionCode.getCode();
		this.message = exceptionCode.getMessage();
	}
}
