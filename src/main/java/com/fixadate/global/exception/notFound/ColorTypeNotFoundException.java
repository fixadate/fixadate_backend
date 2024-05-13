package com.fixadate.global.exception.notFound;

import com.fixadate.global.exception.ExceptionCode;

public non-sealed class ColorTypeNotFoundException extends NotFoundException {
	public ColorTypeNotFoundException(ExceptionCode exceptionCode) {
		super(exceptionCode);
	}
}
