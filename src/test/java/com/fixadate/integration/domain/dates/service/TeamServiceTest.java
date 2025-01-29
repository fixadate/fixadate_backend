package com.fixadate.integration.domain.dates.service;

import static com.fixadate.domain.auth.entity.OAuthProvider.translateStringToOAuthProvider;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fixadate.domain.auth.dto.request.MemberRegisterRequest;
import com.fixadate.domain.auth.service.AuthService;
import com.fixadate.domain.dates.dto.TeamCreateRequest;
import com.fixadate.domain.dates.entity.Teams;
import com.fixadate.domain.dates.repository.TeamRepository;
import com.fixadate.domain.dates.service.TeamService;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.service.repository.MemberRepository;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import javax.sql.DataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

//@ExtendWith(DataClearExtension.class)
@SpringBootTest
//@Testcontainers
class TeamServiceTest {

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AuthService authService;

    private static final String MESSAGE = "message";
    
    @Container
    static MySQLContainer mySQLContainer = new MySQLContainer<>("mysql:8.0.31");

    @BeforeAll
    static void initDataBase(@Autowired DataSource dataSource) {
        try (Connection conn = dataSource.getConnection()) {
            mySQLContainer.start();
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("/sql/init/dropTable.sql"));
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("/sql/init/dates_test.sql"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

//    @AfterAll
//    static void stopContainers() {
//        mySQLContainer.stop();
//    }

    @Nested
    @DisplayName("Team 저장 테스트")
    class CreateTeamTest {
        
        @DisplayName("정상적으로 팀이 저장되는 경우")
        @ParameterizedTest(name = "{index}번째 팀 생성")
        @CsvSource(value = {
            "DevTeam, 개발팀입니다",
            "DesignTeam, 디자인팀입니다",
            "MarketingTeam, 마케팅팀입니다",
            "SalesTeam, 영업팀입니다",
            "HRTeam, 인사팀입니다"
        })
        void createTeamTestSuccess(
            @AggregateWith(TeamAggregator.class) TeamCreateRequest teamCreateRequest
        ) {
            MemberRegisterRequest registerRequest = new MemberRegisterRequest(
                "105", "google", "emily", "617", "chris", "19921005", "female", "manager", "orange", "kevin@naver.com", "MEMBER"
            );
            authService.registerMember(registerRequest);

            Optional<Member> memberOptional = memberRepository.findMemberByOauthPlatformAndEmailAndName(
                translateStringToOAuthProvider(registerRequest.oauthPlatform()),
                registerRequest.email(), registerRequest.name()
            );
            Member member = memberOptional.orElse(null);

            Assertions.assertDoesNotThrow(() -> teamService.createTeam(member, teamCreateRequest));

            Optional<Teams> teamOptional = teamRepository.findByName(teamCreateRequest.name());
            
            assertTrue(teamOptional.isPresent());
            Teams team = teamOptional.get();

            assertAll(
                () -> assertEquals(teamCreateRequest.name(), team.getName()),
                () -> assertEquals(teamCreateRequest.description(), team.getDescription())
            );
        }
    }

    static class TeamAggregator implements ArgumentsAggregator {
        @Override
        public Object aggregateArguments(ArgumentsAccessor argumentsAccessor, ParameterContext parameterContext) throws
            ArgumentsAggregationException {
            return new TeamCreateRequest(
                argumentsAccessor.getString(0),
                argumentsAccessor.getString(1)
            );
        }
    }
}