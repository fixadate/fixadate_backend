package com.fixadate.global.exception.badRequest;

import com.fixadate.global.exception.ExceptionCode;

public non-sealed class EncryptionBadRequestException extends BadRequestException {
	public EncryptionBadRequestException(ExceptionCode exceptionCode) {
		super(exceptionCode);
	}
}
