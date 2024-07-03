package com.fixadate.global.exception.badRequest;

import static com.fixadate.global.exception.badRequest.RedisRequestException.*;

import com.fixadate.global.exception.ExceptionCode;

import lombok.Getter;

@Getter
public sealed class BadRequestException extends RuntimeException
	permits InvalidTimeException, GoogleIOExcetption, EncryptionBadRequestException, TagBadRequestException,
	RedisConnectionException, SerializationException, RedisExecutionException, OAuthPlatformBadRequest,
	RedisException {
	private final int code;
	private final String message;

	public BadRequestException(final ExceptionCode exceptionCode) {
		this.code = exceptionCode.getCode();
		this.message = exceptionCode.getMessage();
	}
}
