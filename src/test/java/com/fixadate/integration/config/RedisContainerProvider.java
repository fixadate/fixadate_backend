package com.fixadate.integration.config;

import com.redis.testcontainers.RedisContainer;

public class RedisContainerProvider {
	private static final String IMAGE_VERSION = "redis:5.0.7-alpine";
	private static RedisContainer INSTANCE;

	private RedisContainerProvider() {
	}

	public static RedisContainer getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new RedisContainer(IMAGE_VERSION);
			INSTANCE.start();
		}
		return INSTANCE;
	}
}