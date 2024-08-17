package com.fixadate.config;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import com.redis.testcontainers.RedisContainer;

final class RedisContainerProvider {

	private static final String IMAGE_VERSION = "redis:5.0.7-alpine";
	private static final String REDIS_PASSWORD = "REDIS_PASSWORD";
	private static final Integer REDIS_PORT = 6379;
	private static final RedisContainer REDIS_CONTAINER;

	private RedisContainerProvider() {
	}

	static {
		REDIS_CONTAINER = new RedisContainer(IMAGE_VERSION)
			.withExposedPorts(REDIS_PORT)
			.withCommand("--requirepass " + REDIS_PASSWORD)
			.withReuse(true);
		REDIS_CONTAINER.start();
	}

	@DynamicPropertySource
	static void registerRedisProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
		registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(REDIS_PORT).toString());
		registry.add("spring.data.redis.password", () -> REDIS_PASSWORD);
	}
}
