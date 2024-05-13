package com.fixadate.global.exception.badRequest;

import com.fixadate.global.exception.ExceptionCode;

import lombok.Getter;

@Getter
public sealed class BadRequestException extends RuntimeException
	permits InvalidTimeException, GoogleIOExcetption, EncryptionBadRequestException, ColorTypeBadRequestException,
	OAuthPlatformBadRequest {
	private final int code;
	private final String message;

	public BadRequestException(final ExceptionCode exceptionCode) {
		this.code = exceptionCode.getCode();
		this.message = exceptionCode.getMessage();
	}
}
