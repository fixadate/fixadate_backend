package com.fixadate.global.exception.badRequest;

import com.fixadate.global.exception.ExceptionCode;

public non-sealed class TagBadRequestException extends BadRequestException {
	public TagBadRequestException(ExceptionCode exceptionCode) {
		super(exceptionCode);
	}
}
