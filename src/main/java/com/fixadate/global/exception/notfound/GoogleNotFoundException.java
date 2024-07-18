package com.fixadate.global.exception.notfound;

import com.fixadate.global.exception.ExceptionCode;

public non-sealed class GoogleNotFoundException extends NotFoundException {
	public GoogleNotFoundException(ExceptionCode exceptionCode) {
		super(exceptionCode);
	}
}
