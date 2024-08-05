package com.fixadate.integration.config;

import org.testcontainers.containers.MySQLContainer;

public class MySqlContainerProvider {
	private static final String IMAGE_VERSION = "mysql:8.0.31";
	private static MySQLContainer<? extends MySQLContainer<?>> INSTANCE;

	private MySqlContainerProvider() {
	}

	public static MySQLContainer<? extends MySQLContainer<?>> getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new MySQLContainer<>(IMAGE_VERSION);
			INSTANCE.start();
		}
		return INSTANCE;
	}
}