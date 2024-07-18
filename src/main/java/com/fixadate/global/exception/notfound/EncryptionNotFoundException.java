package com.fixadate.global.exception.notfound;

import com.fixadate.global.exception.ExceptionCode;

public non-sealed class EncryptionNotFoundException extends NotFoundException {
	public EncryptionNotFoundException(ExceptionCode exceptionCode) {
		super(exceptionCode);
	}
}
