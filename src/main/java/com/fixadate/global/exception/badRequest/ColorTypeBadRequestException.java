package com.fixadate.global.exception.badRequest;

import com.fixadate.global.exception.ExceptionCode;

public non-sealed class ColorTypeBadRequestException extends BadRequestException {
	public ColorTypeBadRequestException(ExceptionCode exceptionCode) {
		super(exceptionCode);
	}
}
