package com.fixadate.config;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class DataClearExtension implements BeforeEachCallback {
	@Override
	public void beforeEach(ExtensionContext extensionContext) throws Exception {
		DataCleaner dataCleaner = getDataCleaner(extensionContext);
		dataCleaner.clear();
	}

	private DataCleaner getDataCleaner(ExtensionContext extensionContext) {
		return SpringExtension.getApplicationContext(extensionContext)
							  .getBean(DataCleaner.class);
	}
}
