package com.fixadate.global.exception.badRequest;

import com.fixadate.global.exception.ExceptionCode;

public non-sealed class InvalidTimeException extends BadRequestException {
	public InvalidTimeException(ExceptionCode exceptionCode) {
		super(exceptionCode);
	}
}
