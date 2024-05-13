package com.fixadate.global.exception.notFound;

import com.fixadate.global.exception.ExceptionCode;

import lombok.Getter;

@Getter
public sealed class NotFoundException extends RuntimeException
	permits AdateNotFoundException, GoogleNotFoundException, EncryptionNotFoundException, ColorTypeNotFoundException,
	MemberNotFoundException {
	private final int code;
	private final String message;

	public NotFoundException(final ExceptionCode exceptionCode) {
		this.code = exceptionCode.getCode();
		this.message = exceptionCode.getMessage();
	}
}
