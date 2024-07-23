package com.fixadate.global.util.constant;

import lombok.Getter;

@Getter
public enum ConstantValue {
	OAUTH_ID("oauthId"),
	ACCESS_TOKEN("accessToken"),
	REFRESH_TOKEN("refreshToken"),
	ACCESS_TYPE("offline"),
	APPLICATION_NAME("fixadate"),
	CHANNEL_TYPE("web_hook"),
	BASE_URL("https://api.fixadate.app"),
	NOTIFICATION_URL("/v1/google/notifications"),
	FILE_PATH("/credentials.json"),
	TOKEN_DIRECTORY_PATH("tokens"),
	CALENDAR_ID("primary"),
	GOOGLE_CALLBACK("/oauth2callback"),
	APPROVAL_PROMPT("force"),
	AUTHORIZATION("Authorization"),
	AUTHORIZATION_BEARER("Bearer "),
	CALENDAR_CANCELLED("cancelled"),
	SYNC_TOKEN_KEY("syncToken"),
	SYNC_SETTINGS("SyncSettings"),
	TRANSPARENCY("transparency"),
	ID("id"),
	BLACK_LIST("blackList"),
	AES_ALGORITHM("AES"),
	CIPHER_INSTANCE("AES/CBC/PKCS5Padding"),
	GOOGLE_CALENDAR("Google Calendar"),
	GOOGLE_CALENDAR_COLOR("4285F4"),
	UTC("UTC"),
	ADATE_WITH_COLON("Adate:");

	private final String value;

	ConstantValue(String value) {
		this.value = value;
	}

}
