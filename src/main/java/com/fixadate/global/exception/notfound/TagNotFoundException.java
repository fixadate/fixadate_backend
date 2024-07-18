package com.fixadate.global.exception.notfound;

import com.fixadate.global.exception.ExceptionCode;

public non-sealed class TagNotFoundException extends NotFoundException {
	public TagNotFoundException(ExceptionCode exceptionCode) {
		super(exceptionCode);
	}
}
