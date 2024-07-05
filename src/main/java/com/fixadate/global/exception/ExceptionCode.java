package com.fixadate.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode {
	/*
	adate -> 1xxx
	tag -> 2xxx
	googleCalendar -> 3xxx
	member -> 4xxx

	team -> 7xxx
	auth -> 8xxx
	other -> 9xxx
	 */
	NOT_FOUND_ADATE_CALENDAR_ID(1001, "요청한 calendarId에 해당하는 Adate가 없습니다."),
	INVALID_MONTH(1002, "월에는 1부터 12사이의 값을 입력해야 합니다."),
	INVALID_LOCALDATE(1003, "잘못된 LocalDate 값입니다."),
	INVALID_START_END_TIME(1004, "종료 시간은 시작 시간보다 빠르면 안됩니다."),
	INVALID_STRING_TO_DATETIME(1005, "문자열을 DateTime으로 변환 하는데 실패했습니다."),
	FAIL_TO_CONNECT_REDIS(1006, "Redis와 연결하는데 실패했습니다."),
	FAIL_TO_SERIALIZATION(1007, "직렬화 하는데 실패했습니다."),
	FAIL_TO_EXECUTE_REDIS_COMMAND(1008, "Redis 명령을 실행하는데 실패했습니다."),
	UNKNOWN_REDIS_EXCEPTION(1009, "Redis 예외가 발생했습니다."),

	ALREADY_EXISTS_TAG(2001, "변경하려고 하는 이름이 이미 존재합니다."),
	NOT_FOUND_TAG_MEMBER_NAME(2002, "요청한 member, name에 해당하는 tag가 없습니다."),
	CAN_NOT_UPDATE_OR_REMOVE_DEFAULT_TAG(2003, "기본으로 생성된 tag을 수정하거나 삭제할 수 없습니다."),

	INVALID_GOOGLE_CALENDAR_REQUEST_EXECUTE(3001, "구글 캘린더의 Request에서 파일에 접근할 수 없습니다."),
	INVALID_GOOGLE_CALENDAR_GET_SYNCTOKEN(3002, "SyncSettingsDataStore에서 SyncToken을 얻는데 실패했습니다."),
	INVALID_GOOGLE_CALENDAR_SET_SYNCTOKEN(3002, "SyncSettingsDataStore에서 SyncToken을 설정하는데 실패했습니다."),
	INVALID_GOOGLE_CALENDAR_DELETE_SYNCTOKEN(3003, "SyncSettingsDataStore에서 SyncToken을 삭제하는데 실패했습니다."),
	INVALID_GOOGLE_CALENDAR_LOAD_SECRETS(3004, "GoogleClientSecrets에서 secrets를 가져오는데 실패했습니다."),
	INVALID_GOOGLE_CALENDAR_WATCH_EXECUTE(3005, "watch에서 채널을 생성하는데 실패했습니다."),
	INVALID_GOOGLE_CALENDAR_LOAD_CREDENTIALS(3006, "flow로부터 credentials를 가져오는데 실패했습니다."),
	INVALID_GOOGLE_CALENDAR_CREATE_AND_STORE_CREDENTIALS(3007, "credentials를 생성하고 flow에 저장하는데 실패했습니다."),
	INVALID_GOOGLE_CALENDAR_STOP(3008, "channel을 중지시키는데 실패했습니다."),
	NOT_FOUND_GOOGLE_CREDENTIALS_CHANNELID(3009, "요청한 channelId에 해당하는 GoogleCredentials가 없습니다."),
	NOT_FOUND_GOOGLE_CREDENTIALS_MEMBER(3010, "요청한 member에 해당하는 GoogleCredentials가 없습니다."),
	NOT_FOUND_HEADER_ID(3011, "header에서 id를 추출하는데 실패했습니다."),

	NOT_FOUND_MEMBER_ID(4001, "요청한 id에 해당하는 member가 없습니다."),
	NOT_FOUND_MEMBER_EMAIL(4002, "요청한 email에 해당하는 member가 없습니다."),

	NOT_FOUND_MEMBER_OAUTHPLATFORM_EMAIL_NAME(8001, "요청한 oauthPlatform, email, name에 해당하는 member가 없습니다."),
	FAIL_TO_SIGNIN(8002, "member의 oauthId와 일치하지 않습니다."),
	ALREADY_EXISTS_MEMBER(8003, "member가 이미 존재합니다."),

	NOT_FOUND_PADDING_OR_ALGORITHM(9001, "AES 알고리즘에서 Cipher 객체를 가져오는데 실패했습니다."),
	INVALID_ALGORITHM_OR_KEY(9002, "AES 알고리즘에서 Cipher를 초기화 하는데 실패했습니다."),
	INVALID_BLOCKSIZE(9003, "AES 알고리즘에서 암호화 또는 복호화의 블록 크기가 알고리즘의 블록 크기와 맞지 않습니다."),
	INVALID_PADDING(9004, "AES 알고리즘에서 복호화 하는 과정에서 패딩이 유효하지 않아 실패했습니다."),
	FAIL_TO_DECODER(9004, "AES 알고리즘에서 복호화를 하는데 실패했습니다."),
	NOT_SUPPORTED_OAUTH_SERVICE(9005, "해당 OAuth 서비스는 제공하지 않습니다."),
	NOT_FOUND_MEMBER_ID_IN_HEADER(9006, "header에서 memberId를 찾을 수 없습니다."),
	INVALID_SQL(9007, "sql 문에서 예외가 발생했습니다."),
	NOT_FOUND_S3_IMG(9008, "저장소에 해당 S3 이미지가 없습니다."),

	EXPIRED_PERIOD_TOKEN(401, "기한이 만료된 토큰입니다."),
	FAIL_TO_VALIDATE_TOKEN(400, "토큰 유효성 검사 중 오류가 발생했습니다."),
	INVALID_TOKEN(400, "올바르지 않은 형식의 토큰입니다."),
	NOT_FOUND_MEMBER_IDENTIFIER(401, "요청한 token에 있는 member의 id가 유효하지 않거나 없습니다."),
	INVALID_TOKEN_BLACKLIST(401, "토큰이 블랙리스트 명단에 포함되어 있습니다."),
	NOT_FOUND_REFRESHTOKEN(401, "요청한 refreshToken을 redis에서 찾는데 실패했습니다.");

	private final int code;
	private final String message;
}
