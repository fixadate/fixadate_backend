package com.fixadate.global.auth.service;

import com.fixadate.config.DataClearExtension;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.repository.MemberRepository;
import com.fixadate.global.auth.dto.request.MemberRegistRequest;
import com.fixadate.global.auth.exception.MemberSigninException;
import com.fixadate.global.auth.exception.UnknownOAuthPlatformException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataIntegrityViolationException;
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
class AuthServiceTest {

    @Autowired
    private AuthService authService;
    @Autowired
    private MemberRepository memberRepository;
    @Container
    static MySQLContainer mySQLContainer = new MySQLContainer<>("mysql:8.0.31");
    @BeforeAll
    static void initDataBase(@Autowired DataSource dataSource) {
        try (Connection conn = dataSource.getConnection()) {
            mySQLContainer.start();
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("/sql/init/dropTable.sql"));
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("/sql/init/member_test.sql"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    static void stopContainers() {
        mySQLContainer.stop();
    }

    @Nested
    @DisplayName("oauthId 검색 테스트")
    class findMemberByOauthIdTest {
        @DisplayName("oauthId가 존재하는 경우")
        @Sql(scripts = "/sql/setup/member_setUp.sql")
        @ParameterizedTest(name = "[{index}] oauthId -> {arguments}")
        @ValueSource(strings = {"123", "2", "3", "4", "5"})
        void findMemberByOauthIdIfMemberExistsTest(String inputs) {
            assertAll(
                    () -> assertDoesNotThrow(() -> authService.findMemberByOAuthId(inputs))
            );
        }

        @DisplayName("조회 시, oauthId가 존재하지 않는 경우 예외 발생")
        @ParameterizedTest(name = "[{index}] oauthId -> {arguments}")
        @ValueSource(strings = {"11", "22", "33", "44", "55"})
        void findMemberByOauthIdIfMemberNotExistTest(String input) {
            assertAll(
                    () -> assertThrows(MemberSigninException.class, () -> authService.findMemberByOAuthId(input))
            );

        }
    }

    @Nested
    @DisplayName("Member 저장 테스트")
    class registMemberTest {
        @DisplayName("동일한 oauthId를 가진 Member가 존재하는 경우 에러 발생")
        @Sql(scripts = "/sql/setup/member_setUp.sql")
        @ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
        @CsvSource(value = {"123, google, yongjun, 213, kevinH, 20000928, male, student, red, img",
                "2, kakao, hannah, 314, alex, 19980512, female, engineer, blue, img",
                "3, apple, david, 415, emma, 20010320, male, designer, green, img",
                "4, kakao, sarah, 516, michael, 19991225, female, developer, yellow, img",
                "5, google, emily, 617, chris, 19921005, female, manager, orange, img"})
        void registMemberTestIfDuplicatedIdExist(@AggregateWith(MemberAggregator.class) MemberRegistRequest memberRegistRequest) {
            assertThrows(DataIntegrityViolationException.class, () -> authService.registMember(memberRegistRequest));
            Member member = authService.findMemberByOAuthId(memberRegistRequest.oauthId());
            assertAll(
                    () -> assertNotEquals(memberRegistRequest.name(), member.getName()),
                    () -> assertNotEquals(memberRegistRequest.nickname(), member.getNickname()),
                    () -> assertNotEquals(memberRegistRequest.birth(), member.getBirth())
            );
        }

        @DisplayName("잘못된 oauthPlatform 값이 들어간 경우 에러 발생")
        @ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
        @CsvSource(value = {"101, naver, yongjun, 213, kevin, 20000928, male, student, red, img",
                "102, git, hannah, 314, alex, 19980512, female, engineer, blue, img",
                "103, microsoft, david, 415, emma, 20010320, male, designer, green, img",
                "104, samsung, sarah, 516, michael, 19991225, female, developer, yellow, img",
                "105, wooabros, emily, 617, chris, 19921005, female, manager, orange, img"})
        void registMemberTestIfoauthPlatformHasInvalidValue(@AggregateWith(MemberAggregator.class) MemberRegistRequest memberRegistRequest) {
            assertAll(
                    () -> assertThrows(UnknownOAuthPlatformException.class, () -> authService.registMember(memberRegistRequest)),
                    () -> assertThrows(MemberSigninException.class, () -> authService.findMemberByOAuthId(memberRegistRequest.oauthId()))
            );
        }

        @DisplayName("저장이 정상적으로 되는 경우")
        @ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
        @CsvSource(value = {"100, google, yongjun, 213, kevin, 20000928, male, student, red, img",
                "101, google, kevin, 314, alex, 19980512, female, engineer, blue, img",
                "102, apple, david, 415, emma, 20010320, male, designer, green, img",
                "103, google, sarah, 516, michael, 19991225, female, developer, yellow, img",
                "104, google, emily, 617, chris, 19921005, female, manager, orange, img"})
        void registMemberTestIfEveryThingsIsOk(@AggregateWith(MemberAggregator.class) MemberRegistRequest memberRegistRequest) {
            assertDoesNotThrow(() -> authService.registMember(memberRegistRequest));
            Member member = authService.findMemberByOAuthId(memberRegistRequest.oauthId());
            assertAll(
                    () -> assertEquals(memberRegistRequest.name(), member.getName()),
                    () -> assertEquals(memberRegistRequest.oauthId(), member.getOauthId()),
                    () -> assertEquals(memberRegistRequest.birth(), member.getBirth()),
                    () -> assertEquals(memberRegistRequest.nickname(), member.getNickname()),
                    () -> assertEquals(memberRegistRequest.gender(), member.getGender()),
                    () -> memberRepository.delete(member)
            );
        }
    }

    static class MemberAggregator implements ArgumentsAggregator {
        @Override
        public Object aggregateArguments(ArgumentsAccessor argumentsAccessor, ParameterContext parameterContext) throws ArgumentsAggregationException {
            return new MemberRegistRequest(argumentsAccessor.getString(0), argumentsAccessor.getString(1), argumentsAccessor.getString(2), argumentsAccessor.getString(3),
                    argumentsAccessor.getString(4), argumentsAccessor.getInteger(5), argumentsAccessor.getString(6), argumentsAccessor.getString(7), argumentsAccessor.getString(8),
                    argumentsAccessor.getString(9));
        }
    }
}