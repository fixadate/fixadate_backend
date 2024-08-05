package com.fixadate.integration.config;

import com.redis.testcontainers.RedisContainer;

abstract class RedisContainerProvider {

	private static final String IMAGE_VERSION = "redis:5.0.7-alpine";
	private static final RedisContainer REDIS_CONTAINER;

	private RedisContainerProvider() {
	}

	static {
		REDIS_CONTAINER = new RedisContainer(IMAGE_VERSION);
		REDIS_CONTAINER.start();
	}
}
