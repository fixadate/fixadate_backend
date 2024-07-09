package com.fixadate.global.facade;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fixadate.global.exception.badRequest.RedisRequestException;

import lombok.RequiredArgsConstructor;

/**
 *
 * @author yongjunhong
 * @since 2024. 7. 9.
 */

@Component
@RequiredArgsConstructor
public class RedisFacade {

	private final RedisTemplate<Object, Object> redisJsonTemplate;
	private final ObjectMapper objectMapper;

	public void setObjectRedis(String key, Object object, Duration duration) {
		try {
			redisJsonTemplate.opsForValue().set(key, object, duration);
		} catch (Exception e) {
			RedisRequestException.handleRedisException(e);
		}
	}

	public <T> T getObjectRedis(String key, Class<T> type) {
		try {
			return objectMapper.convertValue(redisJsonTemplate.opsForValue().get(key), type);
		} catch (Exception e) {
			RedisRequestException.handleRedisException(e);
			return null;
		}
	}

	public <T> T getAndDeleteObjectRedis(String key, Class<T> type) {
		try {
			return objectMapper.convertValue(redisJsonTemplate.opsForValue().getAndDelete(key), type);
		} catch (Exception e) {
			RedisRequestException.handleRedisException(e);
			return null;
		}
	}
}
