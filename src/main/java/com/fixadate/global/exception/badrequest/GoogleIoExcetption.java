package com.fixadate.global.exception.badrequest;

import com.fixadate.global.exception.ExceptionCode;

public non-sealed class GoogleIoExcetption extends BadRequestException {
	public GoogleIoExcetption(ExceptionCode exceptionCode) {
		super(exceptionCode);
	}
}
