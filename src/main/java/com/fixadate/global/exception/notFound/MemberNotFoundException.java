package com.fixadate.global.exception.notFound;

import com.fixadate.global.exception.ExceptionCode;

public non-sealed class MemberNotFoundException extends NotFoundException {
	public MemberNotFoundException(ExceptionCode exceptionCode) {
		super(exceptionCode);
	}
}
