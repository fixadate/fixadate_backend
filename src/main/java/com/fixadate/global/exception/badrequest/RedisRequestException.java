package com.fixadate.global.exception.badrequest;

import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.serializer.SerializationException;

import com.fixadate.global.exception.ExceptionCode;

import io.lettuce.core.RedisCommandExecutionException;

public class RedisRequestException {

	public static void handleRedisException(Exception exception) {
		if (exception instanceof RedisConnectionFailureException || exception instanceof RedisConnectionException) {
			throw new RedisConnectionException(ExceptionCode.FAIL_TO_CONNECT_REDIS);
		} else if (exception instanceof SerializationException || exception instanceof IllegalArgumentException) {
			throw new RedisSerializationException(ExceptionCode.FAIL_TO_SERIALIZATION);
		} else if (exception instanceof RedisSystemException || exception instanceof RedisCommandExecutionException) {
			throw new RedisExecutionException(ExceptionCode.FAIL_TO_EXECUTE_REDIS_COMMAND);
		} else {
			throw new RedisException(ExceptionCode.UNKNOWN_REDIS_EXCEPTION);
		}
	}

	static non-sealed class RedisConnectionException extends BadRequestException {

		public RedisConnectionException(ExceptionCode exceptionCode) {
			super(exceptionCode);
		}
	}

	static non-sealed class RedisSerializationException extends BadRequestException {

		public RedisSerializationException(ExceptionCode exceptionCode) {
			super(exceptionCode);
		}
	}

	static non-sealed class RedisExecutionException extends BadRequestException {

		public RedisExecutionException(ExceptionCode exceptionCode) {
			super(exceptionCode);
		}
	}

	static non-sealed class RedisException extends BadRequestException {

		public RedisException(ExceptionCode exceptionCode) {
			super(exceptionCode);
		}
	}

}
