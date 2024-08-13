package com.fixadate.config;

import org.springframework.stereotype.Component;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@Component
@SuppressWarnings("NonAsciiCharacters")
public class RedisContainerProvider {

	private static final DockerImageName DOCKER_IMAGE_NAME = DockerImageName.parse("redis:7.0.15-alpine");
	private static final Integer REDIS_PORT = 6379;
	private static final GenericContainer<?> REDIS_CONTAINER;

	static {
		REDIS_CONTAINER = new GenericContainer<>(DOCKER_IMAGE_NAME)
			.withExposedPorts(REDIS_PORT);

		REDIS_CONTAINER.start();

		System.setProperty("spring.data.redis.host", REDIS_CONTAINER.getHost());
		System.setProperty("spring.data.redis.port", REDIS_CONTAINER.getMappedPort(REDIS_PORT).toString());
	}
}
