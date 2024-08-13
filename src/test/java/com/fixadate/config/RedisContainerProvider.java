package com.fixadate.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

final class RedisContainerProvider {

	private static final DockerImageName DOCKER_IMAGE_NAME = DockerImageName.parse("redis:7.0");
	private static final Integer REDIS_PORT = 6379;
	private static final GenericContainer<?> REDIS_CONTAINER;

	static {
		REDIS_CONTAINER = new GenericContainer<>(DOCKER_IMAGE_NAME)
			.withExposedPorts(REDIS_PORT);
		REDIS_CONTAINER.start();
	}

	@DynamicPropertySource
	static void registerRedisProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
		registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(REDIS_PORT));
	}

	@Test
	void givenRedisContainerConfiguredWithDynamicProperties_whenCheckingRunningStatus_thenStatusIsRunning() {
		assertThat(REDIS_CONTAINER.isRunning()).isTrue();
	}
}
