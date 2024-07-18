package com.fixadate.domain.googlecalendar.protocol;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DatabaseConnection {

	private static String DB_DRIVER;
	private static String DB_URL;
	private static String DB_USER_NAME;
	private static String DB_PASSWORD;

	@Value("${spring.datasource.driver-class-name}")
	void setDbDriver(String driver) {
		this.DB_DRIVER = driver;
	}

	@Value("${spring.datasource.url}")
	void setDbUrl(String url) {
		this.DB_URL = url;
	}

	@Value("${spring.datasource.username}")
	void setDbUserName(String userName) {
		this.DB_USER_NAME = userName;
	}

	@Value("${spring.datasource.password}")
	void setDbPassword(String password) {
		this.DB_PASSWORD = password;
	}

	protected static Connection initializeDatabase()
		throws SQLException, ClassNotFoundException {
		Class.forName(DB_DRIVER);
		Connection con = DriverManager.getConnection(DB_URL, DB_USER_NAME, DB_PASSWORD);
		return con;
	}
}
