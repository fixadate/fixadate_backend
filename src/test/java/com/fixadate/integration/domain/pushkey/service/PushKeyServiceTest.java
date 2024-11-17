package com.fixadate.integration.domain.pushkey.service;

import static com.fixadate.global.exception.ExceptionCode.NOT_FOUND_MEMBER_ID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fixadate.config.DataClearExtension;
import com.fixadate.domain.pushkey.repository.PushKeyRepository;
import com.fixadate.domain.pushkey.service.PushKeyService;
import com.fixadate.global.exception.notfound.MemberNotFoundException;

@ExtendWith(DataClearExtension.class)
@SpringBootTest
@Testcontainers
class PushKeyServiceTest {

	@Autowired
	private PushKeyService pushKeyService;
	@Autowired
	private PushKeyRepository pushKeyRepository;
	private static final String MESSAGE = "message";

	@Nested
	@DisplayName("pushKey 생성 테스트")
	class RegisterPushKeyTest {

		@DisplayName("pushKey 정상 저장되는 경우")
		@Sql(scripts = "/sql/setup/member_register_setup.sql")
		@ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
		@CsvSource(value = {
			"'examplePushKey1','101'",
			"'examplePushKey2','102'",
			"'examplePushKey3','103'",
			"'examplePushKey4','104'",
			"'examplePushKey5','105'",

		})
		void generatePushKeyTestSuccess(String pushKey, String memberId) {
			assertAll(
				() -> assertDoesNotThrow(() -> pushKeyService.registerPushKey(pushKey, memberId)),
				() -> assertTrue(pushKeyRepository.findPushKeyByPushKey(pushKey).isPresent())
			);
		}

		@DisplayName("pushKey 업데이트 하는 경우")
		@Sql(scripts = {"/sql/setup/pushKey_setup.sql", "/sql/setup/member_register_setup.sql"})
		@ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
		@CsvSource(value = {
			"'examplePushKey6','101'",
			"'examplePushKey7','102'",
			"'examplePushKey8','103'",
			"'examplePushKey9','104'",
			"'examplePushKey10','105'",

		})
		void generatePushKeyTestUpdate(String pushKey, String memberId) {
			assertAll(
				() -> assertDoesNotThrow(() -> pushKeyService.registerPushKey(pushKey, memberId)),
				() -> assertTrue(pushKeyRepository.findPushKeyByPushKey(pushKey).isPresent())
			);
		}

		@DisplayName("pushKey member가 존재하지 않는 경우")
		@Sql(scripts = "/sql/setup/member_register_setup.sql")
		@ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
		@CsvSource(value = {
			"'examplePushKey1','1'",
			"'examplePushKey2','2'",
			"'examplePushKey3','3'",
			"'examplePushKey4','4'",
			"'examplePushKey5','5'",
		})
		void generatePushKeyTestIfMemberNotExists(String pushKey, String memberId) {
			assertAll(
				() -> assertThatThrownBy(() -> pushKeyService.registerPushKey(pushKey, memberId))
					.isInstanceOf(MemberNotFoundException.class)
					.extracting(MESSAGE)
					.isEqualTo(NOT_FOUND_MEMBER_ID.getMessage()),
				() -> assertFalse(pushKeyRepository.findPushKeyByPushKey(pushKey).isPresent())
			);
		}
	}
}
