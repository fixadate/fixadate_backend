package com.fixadate.global.exception.badRequest;

import com.fixadate.global.exception.ExceptionCode;

public non-sealed class GoogleIOExcetption extends BadRequestException {
	public GoogleIOExcetption(ExceptionCode exceptionCode) {
		super(exceptionCode);
	}
}
