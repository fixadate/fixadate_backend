package com.fixadate.global.versionCheck.entity;

/**
 *
 * @author yongjunhong
 * @since 2024. 7. 11.
 */
public enum UpdateType {
	FORCE_UPDATE("30"),
	SELECT_UPDATE("20"),
	NO_UPDATE("10");

	private final String type;

	UpdateType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
