package com.fixadate.global.exception.notFound;

import com.fixadate.global.exception.ExceptionCode;

public non-sealed class AdateNotFoundException extends NotFoundException {
	public AdateNotFoundException(ExceptionCode exceptionCode) {
		super(exceptionCode);
	}
}
