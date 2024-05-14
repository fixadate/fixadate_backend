package com.fixadate.integration.config;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
public class DataCleaner {

	private static final String FOREIGN_KEY_CHECK_FORMAT = "SET FOREIGN_KEY_CHECKS = %d";
	private static final String TRUNCATE_FORMAT = "TRUNCATE TABLE %s";

	@PersistenceContext
	private EntityManager entityManager;

	public List<String> findDatabaseTableNames() {
		List<String> tableInfos = entityManager.createNativeQuery("SHOW TABLES").getResultList();
		return tableInfos;
	}

	@Transactional
	public void clear() {
		List<String> tableNames = findDatabaseTableNames();
		entityManager.clear();
		truncate(tableNames);
	}

	private void truncate(List<String> tableNames) {
		//외래키 체크 비활성화
		entityManager.createNativeQuery(String.format(FOREIGN_KEY_CHECK_FORMAT, 0)).executeUpdate();
		for (String tableName : tableNames) {
			entityManager.createNativeQuery(String.format(TRUNCATE_FORMAT, tableName)).executeUpdate();
		}
		//외래키 체크 활성화
		entityManager.createNativeQuery(String.format(FOREIGN_KEY_CHECK_FORMAT, 1)).executeUpdate();
	}

}