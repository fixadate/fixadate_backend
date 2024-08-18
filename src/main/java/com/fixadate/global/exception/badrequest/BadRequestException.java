package com.fixadate.global.exception.badrequest;

import static com.fixadate.global.exception.badrequest.RedisRequestException.*;

import com.fixadate.global.exception.ExceptionCode;

import lombok.Getter;

@Getter
public sealed class BadRequestException extends RuntimeException
	permits InvalidTimeException, GoogleIoExcetption, EncryptionBadRequestException, TagBadRequestException,
	RedisConnectionException, RedisSerializationException, RedisExecutionException, OAuthPlatformBadRequest,
	RedisException {
	private final int code;
	private final String message;

	public BadRequestException(final ExceptionCode exceptionCode) {
		this.code = exceptionCode.getCode();
		this.message = exceptionCode.getMessage();
	}
}
