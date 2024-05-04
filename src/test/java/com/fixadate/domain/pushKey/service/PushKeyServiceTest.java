package com.fixadate.domain.pushKey.service;

import com.fixadate.config.DataClearExtension;
import com.fixadate.domain.member.exception.MemberNotFoundException;
import com.fixadate.domain.pushKey.repository.PushKeyRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(DataClearExtension.class)
@SpringBootTest
@Testcontainers
class PushKeyServiceTest {

    @Autowired
    private PushKeyService pushKeyService;
    @Autowired
    private PushKeyRepository pushKeyRepository;

    @Container
    static MySQLContainer mySQLContainer = new MySQLContainer<>("mysql:8.0.31");

    @BeforeAll
    static void initDataBase(@Autowired DataSource dataSource) {
        try (Connection conn = dataSource.getConnection()) {
            mySQLContainer.start();
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("/sql/init/dropTable.sql"));
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("/sql/init/pushKey_test.sql"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    static void stopContainers() {
        mySQLContainer.stop();
    }

    @Nested
    @DisplayName("pushKey 생성 테스트")
    class registPushKeyTest {

        @DisplayName("pushKey 정상 저장되는 경우")
        @Sql(scripts = "/sql/setup/member_regist_setup.sql")
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
                    () -> assertDoesNotThrow(() -> pushKeyService.registPushKey(pushKey, memberId)),
                    () -> assertTrue(pushKeyRepository.findPushKeyByPushKey(pushKey).isPresent())
            );
        }

        @DisplayName("pushKey 업데이트 하는 경우")
        @Sql(scripts = {"/sql/setup/pushKey_setup.sql", "/sql/setup/member_regist_setup.sql"})
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
                    () -> assertDoesNotThrow(() -> pushKeyService.registPushKey(pushKey, memberId)),
                    () -> assertTrue(pushKeyRepository.findPushKeyByPushKey(pushKey).isPresent())
            );
        }

        @DisplayName("pushKey member가 존재하지 않는 경우")
        @Sql(scripts = "/sql/setup/member_regist_setup.sql")
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
                    () -> assertThrows(MemberNotFoundException.class, () -> pushKeyService.registPushKey(pushKey, memberId)),
                    () -> assertFalse(pushKeyRepository.findPushKeyByPushKey(pushKey).isPresent())
            );
        }
    }
}