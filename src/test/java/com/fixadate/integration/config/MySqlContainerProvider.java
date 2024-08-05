package com.fixadate.integration.config;

import org.testcontainers.containers.MySQLContainer;

abstract class MySqlContainerProvider {

	private static final MySQLContainer MY_SQL_CONTAINER;
	private static final String IMAGE_VERSION = "mysql:8.0.31";


	private MySqlContainerProvider() {
	}

	static {
		MY_SQL_CONTAINER = new MySQLContainer(IMAGE_VERSION);
		MY_SQL_CONTAINER.start();
	}
}
