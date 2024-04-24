package com.fixadate.domain.colortype.service;

import com.fixadate.config.DataClearExtension;
import com.fixadate.domain.colortype.dto.request.ColorTypeRequest;
import com.fixadate.domain.colortype.dto.request.ColorTypeUpdateRequest;
import com.fixadate.domain.colortype.entity.ColorType;
import com.fixadate.domain.colortype.exception.ColorTypeDuplicatedException;
import com.fixadate.domain.colortype.exception.ColorTypeNotFoundException;
import com.fixadate.domain.colortype.repository.ColorTypeRepository;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.repository.MemberRepository;
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
import org.junit.jupiter.params.provider.ValueSource;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(DataClearExtension.class)
@SpringBootTest
@Testcontainers
@Slf4j
class ColorTypeServiceTest {

    @Autowired
    private ColorTypeRepository colorTypeRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ColorTypeService colorTypeService;

    @Container
    static MySQLContainer mySQLContainer = new MySQLContainer<>("mysql:8.0.31");

    @BeforeAll
    static void initDataBase(@Autowired DataSource dataSource) {
        try (Connection conn = dataSource.getConnection()) {
            mySQLContainer.start();
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("/sql/init/dropTable.sql"));
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("/sql/init/colorType_test.sql"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    static void stopContainers() {
        mySQLContainer.stop();
    }

    @Nested
    @DisplayName("colorType 저장 테스트")
    class registColorTypeTest {

        @DisplayName("모든 조건에 문제가 없는 경우")
        @Sql(scripts = "/sql/setup/colorType_setup.sql")
        @ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
        @CsvSource(value = {
                "red, 회사",
                "blue, 동아리",
                "black, 스터디",
                "purple, 프로젝트",
                "pink, 연애"
        })
        void registColorType(@AggregateWith(ColorTypeRequestAggregator.class) ColorTypeRequest colorTypeRequest) {
            Optional<Member> memberOptional = memberRepository.findMemberById(1L);
            assertNotNull(memberOptional.get());

            assertDoesNotThrow(() -> colorTypeService.insertColorType(memberOptional.get(), colorTypeRequest));

            Optional<ColorType> colorTypeOptional = colorTypeRepository.findColorTypeByColor(colorTypeRequest.color());
            List<ColorType> colorTypes = colorTypeRepository.findColorTypesByMember(memberOptional.get());

            assertAll(
                    () -> assertTrue(colorTypeOptional.isPresent()),
                    () -> assertEquals(colorTypeRequest.name(), colorTypeOptional.get().getName()),
                    () -> assertEquals(colorTypeRequest.color(), colorTypeOptional.get().getColor()),
                    () -> assertEquals(2, colorTypes.size())
            );
        }

        @DisplayName("중복된 color가 이미 있는 경우")
        @Sql(scripts = "/sql/setup/colorType_setup.sql")
        @ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
        @CsvSource(value = {
                "yellow, 회사",
                "violet, 동아리",
                "orange, 스터디",
                "green, 프로젝트",
                "white, 연애"
        })
        void registColorTypeTestIfDuplicatedColorExists(@AggregateWith(ColorTypeRequestAggregator.class) ColorTypeRequest colorTypeRequest) {
            Optional<Member> memberOptional = memberRepository.findMemberById(1L);
            assertNotNull(memberOptional.get());

            assertThrows(ColorTypeDuplicatedException.class, () -> colorTypeService.insertColorType(memberOptional.get(), colorTypeRequest));
            List<ColorType> colorTypes = colorTypeRepository.findColorTypesByMember(memberOptional.get());

            assertEquals(1, colorTypes.size()); // 기존에 저장된 데이터 때문에 1이다.
        }
    }

    @Nested
    @DisplayName("member를 이용한 colorType 조회")
    class checkColorTest {

        @DisplayName("member가 저장한 colorType이 존재하는 경우")
        @Sql(scripts = "/sql/setup/colorType_setup.sql")
        @ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
        @ValueSource(longs = {1L, 2L, 3L, 4L, 5L})
        void checkColorTestIfColorTypeExists(long input) {
            Optional<Member> memberOptional = memberRepository.findMemberById(input);

            assertAll(
                    () -> assertTrue(memberOptional.isPresent()),
                    () -> assertEquals(1, colorTypeService.getColorTypeResponses(memberOptional.get()).size())
            );
        }

        @DisplayName("member가 저장한 colorType이 존재하지 않는 경우")
        @Sql(scripts = "/sql/setup/colorType_setup.sql")
        @ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
        @ValueSource(longs = {6L, 7L, 8L, 9L, 10L})
        void checkColorTestIfColorTypeNotExists(long input) {
            Optional<Member> memberOptional = memberRepository.findMemberById(input);

            assertAll(
                    () -> assertTrue(memberOptional.isPresent()),
                    () -> assertEquals(0, colorTypeService.getColorTypeResponses(memberOptional.get()).size())
            );
        }
    }

    @Nested
    @DisplayName("updateColorType 테스트")
    class updateColorTypeTest {

        @DisplayName("성공적으로 업데이트를 한 경우")
        @Sql(scripts = "/sql/setup/colorType_setup.sql")
        @ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
        @CsvSource(value = {
                "yellow, newColor1, newName1",
                "violet, newColor2, newName2",
                "white, newColor3, newName3",
                "orange, newColor4, newName4",
                "green, newColor5, newName5"
        })
        void updateColorNameIfColorExists(@AggregateWith(ColorTypeUpdateRequestAggregator.class) ColorTypeUpdateRequest colorTypeUpdateRequest) {
            assertDoesNotThrow(() -> colorTypeService.updateColorType(colorTypeUpdateRequest));
            Optional<ColorType> colorTypeByColor = colorTypeRepository.findColorTypeByColor(colorTypeUpdateRequest.newColor());

            assertAll(
                    () -> assertTrue(colorTypeByColor.isPresent()),
                    () -> assertEquals(colorTypeUpdateRequest.newColor(), colorTypeByColor.get().getColor()),
                    () -> assertEquals(colorTypeUpdateRequest.newName(), colorTypeByColor.get().getName())
            );
        }

        @DisplayName("같은 색으로 업데이트를 한 경우")
        @Sql(scripts = "/sql/setup/colorType_setup.sql")
        @ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
        @CsvSource(value = {
                "yellow, yellow, newName1",
                "violet, violet, newName2",
                "white, white, newName3",
                "orange, orange, newName4",
                "green, green, newName5"
        })
        void updateColorNameToSameColor(@AggregateWith(ColorTypeUpdateRequestAggregator.class) ColorTypeUpdateRequest colorTypeUpdateRequest) {
            assertAll(
                    () -> assertThrows(ColorTypeDuplicatedException.class, () -> colorTypeService.updateColorType(colorTypeUpdateRequest)),
                    () -> assertTrue(colorTypeRepository
                            .findColorTypeByColor(colorTypeUpdateRequest.color()).isPresent())
            );
        }

        @DisplayName("이미 존재하는 색으로 업데이트를 한 경우")
        @Sql(scripts = "/sql/setup/colorType_setup.sql")
        @ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
        @CsvSource(value = {
                "yellow, brown, newName1",
                "violet, silver, newName2",
                "white, cyan, newName3",
                "orange, magenta, newName4",
                "green, maroon, newName5"
        })
        void updateColorNameToExistsColor(@AggregateWith(ColorTypeUpdateRequestAggregator.class) ColorTypeUpdateRequest colorTypeUpdateRequest) {
            assertThrows(ColorTypeDuplicatedException.class, () -> colorTypeService.updateColorType(colorTypeUpdateRequest));
            assertAll(
                    () -> assertTrue(colorTypeRepository
                            .findColorTypeByColor(colorTypeUpdateRequest.color()).isPresent()),
                    () -> assertTrue(colorTypeRepository
                            .findColorTypeByColor(colorTypeUpdateRequest.newColor()).isPresent())
            );
        }

        @DisplayName("color가 존재하지 않는 경우")
        @Sql(scripts = "/sql/setup/colorType_setup.sql")
        @ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
        @CsvSource(value = {
                "red, newColor1, newName1",
                "blue, newColor2, newName2",
                "gray, newColor3, newName3",
                "purple, newColor4, newName4",
                "skyblue, newColor5, newName5"
        })
        void updateColorNameIfColorNotExists(@AggregateWith(ColorTypeUpdateRequestAggregator.class) ColorTypeUpdateRequest colorTypeUpdateRequest) {
            assertThrows(ColorTypeNotFoundException.class, () -> colorTypeService.updateColorType(colorTypeUpdateRequest));
        }
    }


    static class ColorTypeRequestAggregator implements ArgumentsAggregator {
        @Override
        public Object aggregateArguments(ArgumentsAccessor argumentsAccessor, ParameterContext parameterContext) throws ArgumentsAggregationException {
            return new ColorTypeRequest(argumentsAccessor.getString(0), argumentsAccessor.getString(1));
        }
    }

    static class ColorTypeUpdateRequestAggregator implements ArgumentsAggregator {
        @Override
        public Object aggregateArguments(ArgumentsAccessor argumentsAccessor, ParameterContext parameterContext) throws ArgumentsAggregationException {
            return new ColorTypeUpdateRequest(argumentsAccessor.getString(0), argumentsAccessor.getString(1),
                    argumentsAccessor.getString(2));
        }
    }
}