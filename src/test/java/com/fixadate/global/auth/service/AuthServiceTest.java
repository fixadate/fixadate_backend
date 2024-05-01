package com.fixadate.global.auth.service;

import com.fixadate.config.DataClearExtension;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.repository.MemberRepository;
import com.fixadate.global.auth.dto.request.MemberOAuthRequest;
import com.fixadate.global.auth.dto.request.MemberRegistRequest;
import com.fixadate.global.auth.exception.MemberSigninException;
import com.fixadate.global.auth.exception.UnknownOAuthPlatformException;
import com.fixadate.util.CreateMemberRegistRequest;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static com.fixadate.global.oauth.entity.OAuthProvider.translateStringToOAuthProvider;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(DataClearExtension.class)
@SpringBootTest
@Testcontainers
@Slf4j
class AuthServiceTest {

    @Autowired
    private AuthService authService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
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
    @DisplayName("Member 저장 테스트")
    class registMemberTest {
        @DisplayName("잘못된 oauthPlatform 값이 들어간 경우 에러 발생")
        @ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
        @CsvSource(value = {"101, naver, yongjun, 213, kevin, 20000928, male, student, red, img, kevin0928@naver.com",
                "102, git, hannah, 314, alex, 19980512, female, engineer, blue, img, kevin@naver.com",
                "103, microsoft, david, 415, emma, 20010320, male, designer, green, img, kevin",
                "104, samsung, sarah, 516, michael, 19991225, female, developer, yellow, img, kev",
                "105, wooabros, emily, 617, chris, 19921005, female, manager, orange, img, k"})
        void registMemberTestIfoauthPlatformHasInvalidValue(@AggregateWith(MemberAggregator.class) MemberRegistRequest memberRegistRequest) {
            assertAll(
                    () -> assertThrows(UnknownOAuthPlatformException.class, () -> authService.registMember(memberRegistRequest))
            );
        }

        @DisplayName("저장이 정상적으로 되는 경우")
        @ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
        @CsvSource(value = {"100, google, yongjun, 213, kevin, 20000928, male, student, red, img, kevin0928@naver.com",
                "101, google, kevin, 314, alex, 19980512, female, engineer, blue, img, kevin09288715@google.com",
                "102, apple, david, 415, emma, 20010320, male, designer, green, img, kevin0928@apache.com",
                "103, google, sarah, 516, michael, 19991225, female, developer, yellow, img, kevin0928@mit.com",
                "104, google, emily, 617, chris, 19921005, female, manager, orange, img, kevin0928@daum.net",})
        void registMemberTestIfEveryThingsIsOk(@AggregateWith(MemberAggregator.class) MemberRegistRequest memberRegistRequest) {
            assertDoesNotThrow(() -> authService.registMember(memberRegistRequest));
            Optional<Member> memberOptional = memberRepository.findMemberByOauthPlatformAndEmailAndName(
                    translateStringToOAuthProvider(memberRegistRequest.oauthPlatform()),
                    memberRegistRequest.email(), memberRegistRequest.name());
            assertTrue(memberOptional.isPresent());
            Member member = memberOptional.get();

            assertAll(
                    () -> assertEquals(memberRegistRequest.name(), member.getName()),
                    () -> assertEquals(memberRegistRequest.birth(), member.getBirth()),
                    () -> assertEquals(memberRegistRequest.nickname(), member.getNickname()),
                    () -> assertEquals(memberRegistRequest.gender(), member.getGender()),
                    () -> memberRepository.delete(member)
            );
            log.info(member.getOauthId());
        }

        @DisplayName("정상적으로 암호화가 되는 경우")
        @ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
        @CsvSource(value = {"100, google, yongjun, 213, kevin, 20000928, male, student, red, img, kevin0928@naver.com",
                "101, google, kevin, 314, alex, 19980512, female, engineer, blue, img, kevin09288715@google.com",
                "102, apple, david, 415, emma, 20010320, male, designer, green, img, kevin0928@apache.com",
                "103, google, sarah, 516, michael, 19991225, female, developer, yellow, img, kevin0928@mit.com",
                "104, google, emily, 617, chris, 19921005, female, manager, orange, img, kevin0928@daum.net",})
        void registMemberTestencryption(@AggregateWith(MemberAggregator.class) MemberRegistRequest memberRegistRequest) {
            assertDoesNotThrow(() -> authService.registMember(memberRegistRequest));
            Optional<Member> memberOptional = memberRepository.findMemberByOauthPlatformAndEmailAndName(
                    translateStringToOAuthProvider(memberRegistRequest.oauthPlatform()),
                    memberRegistRequest.email(), memberRegistRequest.name());
            assertTrue(memberOptional.isPresent());
            Member member = memberOptional.get();
            assertAll(
                    () -> assertTrue(member.getOauthId().startsWith("{bcrypt}"), "oauthId doesn't start with {bcrypt}"),
                    () -> assertTrue(passwordEncoder.matches(memberRegistRequest.oauthId(), member.getOauthId()))
            );
        }
    }

    @Nested
    @DisplayName("signin 테스트")
    class signinTest {
        @DisplayName("멤버의 이름이 존재하지 않는 경우")
        @ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
        @CsvSource(value = {
                "123, example1, hong@example.com, google",
                "2, mysql, muny@example.com, kakao",
                "3, redis, kim@example.com, kakao",
                "4, valkey, karina@example.com, google",
                "5, spring, down@example.com, apple"
        })
        void signinTestIfNameNotExists(@AggregateWith(OAuthAggregator.class) MemberOAuthRequest memberOAuthRequest) {
            registMember();
            assertThrows(MemberSigninException.class, () -> authService.signIn(memberOAuthRequest));
        }

        @DisplayName("멤버의 이메일이 존재하지 않는 경우")
        @ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
        @CsvSource(value = {
                "123, hong, spring@example.com, google",
                "2, muny, dex@example.com, kakao",
                "3, kim, redis@example.com, kakao",
                "4, karina, karina@apache.com, google",
                "5, down', down@mit.com, apple"
        })
        void signinTestIfEmailNotExists(@AggregateWith(OAuthAggregator.class) MemberOAuthRequest memberOAuthRequest) {
            registMember();
            assertThrows(MemberSigninException.class, () -> authService.signIn(memberOAuthRequest));
        }

        @DisplayName("잘못 된 oauthPlatform 값인 경우")
        @ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
        @CsvSource(value = {
                "678, park, summer@example.com, samsung",
                "901, lee, autumn@example.com, naver",
                "345, choi, winter@example.com, cheeze",
                "567, jang, breeze@example.com, intel",
                "890, song, frost@example.com, micosoft"
        })
        void signinTestIfMemberNotExists(@AggregateWith(OAuthAggregator.class) MemberOAuthRequest memberOAuthRequest) {
            registMember();
            assertThrows(UnknownOAuthPlatformException.class, () -> authService.signIn(memberOAuthRequest));
        }

        @DisplayName("잘못된 oauthId 값인 경우")
        @ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
        @CsvSource(value = {
                "101, hong, hong@example.com, google",
                "102, muny, muny@example.com, google",
                "103, kim, kim@example.com, google",
                "104, karina, karina@example.com, google",
                "105, down, down@example.com, google"
        })
        void signinTestIfOAuthIdIsInvalid(@AggregateWith(OAuthAggregator.class) MemberOAuthRequest memberOAuthRequest) {
            registMember();
            assertThrows(MemberSigninException.class, () -> authService.signIn(memberOAuthRequest));
        }

        @DisplayName("성공적으로 로그인을 한 경우")
        @ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
        @CsvSource(value = {
                "123, hong, hong@example.com, google",
                "2, muny, muny@example.com, google",
                "3, kim, kim@example.com, google",
                "4, karina, karina@example.com, google",
                "5, down, down@example.com, google"
        })
        void signinTestIfSuccess(@AggregateWith(OAuthAggregator.class) MemberOAuthRequest memberOAuthRequest) {
            registMember();
            assertDoesNotThrow(() -> authService.signIn(memberOAuthRequest));
        }
    }

    private void registMember() {
        List<MemberRegistRequest> dto = CreateMemberRegistRequest.registMember();
        for (MemberRegistRequest m : dto) {
            assertDoesNotThrow(() -> authService.registMember(m));
        }
    }


    static class MemberAggregator implements ArgumentsAggregator {
        @Override
        public Object aggregateArguments(ArgumentsAccessor argumentsAccessor, ParameterContext parameterContext) throws ArgumentsAggregationException {
            return new MemberRegistRequest(argumentsAccessor.getString(0), argumentsAccessor.getString(1),
                    argumentsAccessor.getString(2), argumentsAccessor.getString(3),
                    argumentsAccessor.getString(4), argumentsAccessor.getInteger(5),
                    argumentsAccessor.getString(6), argumentsAccessor.getString(7),
                    argumentsAccessor.getString(8), argumentsAccessor.getString(9),
                    argumentsAccessor.getString(10));
        }
    }

    static class OAuthAggregator implements ArgumentsAggregator {
        @Override
        public Object aggregateArguments(ArgumentsAccessor argumentsAccessor, ParameterContext parameterContext) throws ArgumentsAggregationException {
            return new MemberOAuthRequest(argumentsAccessor.getString(0), argumentsAccessor.getString(1),
                    argumentsAccessor.getString(2), argumentsAccessor.getString(3));
        }
    }

}