package com.fixadate.global.exception.badRequest;

import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.RedisSystemException;

import com.fixadate.global.exception.ExceptionCode;

import io.lettuce.core.RedisCommandExecutionException;

/**
 *
 * @author yongjunhong
 * @since 2024. 7. 3.
 */
public class RedisRequestException {

	public static void handleRedisException(Exception e) {
		if (e instanceof RedisConnectionFailureException || e instanceof RedisConnectionException) {
			throw new RedisConnectionException(ExceptionCode.FAIL_TO_CONNECT_REDIS);
		} else if (e instanceof SerializationException || e instanceof IllegalArgumentException) {
			throw new SerializationException(ExceptionCode.FAIL_TO_SERIALIZATION);
		} else if (e instanceof RedisSystemException || e instanceof RedisCommandExecutionException) {
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

	static non-sealed class SerializationException extends BadRequestException {

		public SerializationException(ExceptionCode exceptionCode) {
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
