package com.fixadate.global.exception.badrequest;

import com.fixadate.global.exception.ExceptionCode;

public non-sealed class EncryptionBadRequestException extends BadRequestException {
	public EncryptionBadRequestException(ExceptionCode exceptionCode) {
		super(exceptionCode);
	}
}
