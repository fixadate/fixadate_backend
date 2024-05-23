package com.fixadate.integration.domain.colortype.service;

import static com.fixadate.global.exception.ExceptionCode.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import javax.sql.DataSource;

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

import com.fixadate.domain.adate.repository.AdateRepository;
import com.fixadate.domain.colortype.dto.request.ColorTypeRequest;
import com.fixadate.domain.colortype.dto.request.ColorTypeUpdateRequest;
import com.fixadate.domain.colortype.entity.ColorType;
import com.fixadate.domain.colortype.repository.ColorTypeRepository;
import com.fixadate.domain.colortype.service.ColorTypeService;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.repository.MemberRepository;
import com.fixadate.global.exception.badRequest.ColorTypeBadRequestException;
import com.fixadate.global.exception.notFound.ColorTypeNotFoundException;
import com.fixadate.integration.config.DataClearExtension;

import jakarta.transaction.Transactional;

@ExtendWith(DataClearExtension.class)
@SpringBootTest
@Testcontainers
@Transactional
class ColorTypeServiceTest {

	@Autowired
	private ColorTypeRepository colorTypeRepository;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private ColorTypeService colorTypeService;
	@Autowired
	private AdateRepository adateRepository;

	private static final String MESSAGE = "message";

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
			Optional<Member> memberOptional = memberRepository.findMemberByEmail("hong@example.com");
			assertDoesNotThrow(() -> colorTypeService.registColorType(memberOptional.get(), colorTypeRequest));

			Optional<ColorType> colorTypeOptional = colorTypeRepository.findColorTypeByNameAndMember(
				colorTypeRequest.name(), memberOptional.get());

			assertAll(
				() -> assertTrue(colorTypeOptional.isPresent()),
				() -> assertEquals(colorTypeRequest.name(), colorTypeOptional.get().getName()),
				() -> assertEquals(colorTypeRequest.color(), colorTypeOptional.get().getColor())
			);
		}

		@DisplayName("중복된 이름이 이미 있는 경우")
		@Sql(scripts = "/sql/setup/colorType_setup.sql")
		@ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
		@CsvSource(value = {
			"yellow, ex1",
			"violet, ex2",
			"orange, ex3",
			"green, ex4",
			"white, ex5"
		})
		void registColorTypeTestIfDuplicatedColorExists(
			@AggregateWith(ColorTypeRequestAggregator.class) ColorTypeRequest colorTypeRequest) {
			Optional<Member> memberOptional = memberRepository.findMemberByEmail("hong@example.com");

			assertAll(
				() -> assertThatThrownBy(() -> colorTypeService.registColorType(memberOptional.get(), colorTypeRequest))
					.isInstanceOf(ColorTypeBadRequestException.class)
					.extracting(MESSAGE)
					.isEqualTo(ALREADY_EXISTS_COLORTYPE.getMessage())
			);
		}
	}

	@Nested
	@DisplayName("member를 이용한 colorType 조회")
	class checkColorTest {

		@DisplayName("member가 저장한 colorType이 존재하는 경우")
		@Sql(scripts = "/sql/setup/colorType_setup.sql")
		@ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
		@ValueSource(strings = {"hong@example.com", "muny@example.com", "kim@example.com", "karina@example.com",
			"down@example.com"})
		void checkColorTestIfColorTypeExists(String input) {
			Optional<Member> memberOptional = memberRepository.findMemberByEmail(input);

			assertAll(
				() -> assertTrue(memberOptional.isPresent()),
				() -> assertFalse(colorTypeService.getColorTypeResponses(memberOptional.get()).isEmpty())
			);
		}

		@DisplayName("member가 저장한 colorType이 존재하지 않는 경우")
		@Sql(scripts = "/sql/setup/colorType_setup.sql")
		@ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
		@ValueSource(strings = {"choi@example.com", "lee@example.com", "park@example.com", "cho@example.com",
			"han@example.com"})
		void checkColorTestIfColorTypeNotExists(String input) {
			Optional<Member> memberOptional = memberRepository.findMemberByEmail(input);
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
			"ex1, newColor1, newName1",
			"ex2, newColor2, newName2",
			"ex3, newColor3, newName3",
			"ex4, newColor4, newName4",
			"ex5, newColor5, newName5"
		})
		void updateColorNameIfColorExists(
			@AggregateWith(ColorTypeUpdateRequestAggregator.class) ColorTypeUpdateRequest colorTypeUpdateRequest) {
			Optional<Member> memberOptional = memberRepository.findMemberByEmail("hong@example.com");
			assertDoesNotThrow(() -> colorTypeService.updateColorType(colorTypeUpdateRequest, memberOptional.get()));
			Optional<ColorType> colorTypeByColor = colorTypeRepository.findColorTypeByNameAndMember(
				colorTypeUpdateRequest.newName(), memberOptional.get());

			assertAll(
				() -> assertEquals(colorTypeUpdateRequest.newColor(), colorTypeByColor.get().getColor()),
				() -> assertEquals(colorTypeUpdateRequest.newName(), colorTypeByColor.get().getName()),
				() -> colorTypeByColor.ifPresent(colorType ->
					colorType.getAdates().forEach(adate ->
						assertEquals(colorTypeUpdateRequest.newColor(), adate.getColor())))
			);

		}

		@DisplayName("성공적으로 이름만 업데이트를 한 경우")
		@Sql(scripts = "/sql/setup/colorType_setup.sql")
		@ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
		@CsvSource(value = {
			"ex1,,newColor1",
			"ex2,,newColor2",
			"ex3,,newColor3",
			"ex4,,newColor4",
			"ex5,,newColor5"
		})
		void updateColorNameOnlyNameIfColorExists(
			@AggregateWith(ColorTypeUpdateRequestAggregator.class) ColorTypeUpdateRequest colorTypeUpdateRequest) {
			Optional<Member> memberOptional = memberRepository.findMemberByEmail("hong@example.com");
			assertDoesNotThrow(() -> colorTypeService.updateColorType(colorTypeUpdateRequest, memberOptional.get()));
			Optional<ColorType> colorTypeByColor = colorTypeRepository.findColorTypeByNameAndMember(
				colorTypeUpdateRequest.newName(), memberOptional.get());

			assertAll(
				() -> assertTrue(colorTypeByColor.isPresent()),
				() -> assertEquals(colorTypeUpdateRequest.newName(), colorTypeByColor.get().getName())
			);
		}

		@DisplayName("같은 이름으로 업데이트를 한 경우")
		@Sql(scripts = "/sql/setup/colorType_setup.sql")
		@ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
		@CsvSource(value = {
			"ex1, yellow, ex1",
			"ex2, violet, ex2",
			"ex3, white, ex3",
			"ex4, orange, ex4",
			"ex5, green, ex5"
		})
		void updateColorNameToSameName(
			@AggregateWith(ColorTypeUpdateRequestAggregator.class) ColorTypeUpdateRequest colorTypeUpdateRequest) {
			Optional<Member> memberOptional = memberRepository.findMemberByEmail("hong@example.com");
			assertAll(
				() -> assertThatThrownBy(
					() -> colorTypeService.updateColorType(colorTypeUpdateRequest, memberOptional.get()))
					.isInstanceOf(ColorTypeBadRequestException.class)
					.extracting(MESSAGE)
					.isEqualTo(ALREADY_EXISTS_COLORTYPE.getMessage()),

				() -> assertTrue(colorTypeRepository
					.findColorTypeByNameAndMember(colorTypeUpdateRequest.name(), memberOptional.get()).isPresent())
			);
		}

		@DisplayName("이미 존재하는 이름으로 업데이트를 한 경우")
		@Sql(scripts = "/sql/setup/colorType_setup.sql")
		@ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
		@CsvSource(value = {
			"ex1, white, ex5",
			"ex2, orange, ex5",
			"ex3, green, ex5",
			"ex4, yellow, ex5",
			"ex5, violet, ex1"
		})
		void updateColorNameToExistsColor(
			@AggregateWith(ColorTypeUpdateRequestAggregator.class) ColorTypeUpdateRequest colorTypeUpdateRequest) {
			Optional<Member> memberOptional = memberRepository.findMemberByEmail("hong@example.com");

			assertAll(
				() -> assertThatThrownBy(
					() -> colorTypeService.updateColorType(colorTypeUpdateRequest, memberOptional.get()))
					.isInstanceOf(ColorTypeBadRequestException.class)
					.extracting(MESSAGE)
					.isEqualTo(ALREADY_EXISTS_COLORTYPE.getMessage()),

				() -> assertTrue(colorTypeRepository
					.findColorTypeByNameAndMember(colorTypeUpdateRequest.name(), memberOptional.get()).isPresent())
			);
		}

		@DisplayName("기본으로 생성된 colorType을 변경하려고 하는 경우")
		@Sql(scripts = "/sql/setup/colorType_setup.sql")
		@ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
		@CsvSource(value = {
			"ex8, NColor, newName1",
			"ex9, NColor, newName2",
			"ex10, NColor, newName3"
		})
		void updateColorNameIfColorTypeisDefault(
			@AggregateWith(ColorTypeUpdateRequestAggregator.class) ColorTypeUpdateRequest colorTypeUpdateRequest) {
			Optional<Member> memberOptional = memberRepository.findMemberByEmail("hong@example.com");

			assertAll(
				() -> assertThatThrownBy(
					() -> colorTypeService.updateColorType(colorTypeUpdateRequest, memberOptional.get()))
					.isInstanceOf(ColorTypeBadRequestException.class)
					.extracting(MESSAGE)
					.isEqualTo(CAN_NOT_UPDATE_OR_REMOVE_DEFAULT_COLORTYPE.getMessage())
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
		void updateColorNameIfColorNotExists(
			@AggregateWith(ColorTypeUpdateRequestAggregator.class) ColorTypeUpdateRequest colorTypeUpdateRequest) {
			Optional<Member> memberOptional = memberRepository.findMemberByEmail("hong@example.com");

			assertThatThrownBy(() -> colorTypeService.updateColorType(colorTypeUpdateRequest, memberOptional.get()))
				.isInstanceOf(ColorTypeNotFoundException.class)
				.extracting(MESSAGE)
				.isEqualTo(NOT_FOUND_COLORTYPE_MEMBER_COLOR.getMessage());
		}
	}

	@Nested
	@DisplayName("color 삭제 테스트")
	class removeColorTest {
		@DisplayName("모든 조건에 문제가 없는 경우")
		@Sql(scripts = "/sql/setup/colorType_setup.sql")
		@ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
		@ValueSource(strings = {
			"ex1",
			"ex2",
			"ex3",
			"ex4",
			"ex5"
		})
		void removeColor_Success(String name) {
			Optional<Member> memberOptional = memberRepository.findMemberByEmail("hong@example.com");
			assertAll(
				() -> assertTrue(memberOptional.isPresent()),
				() -> assertDoesNotThrow(() -> colorTypeService.removeColor(name, memberOptional.get())),
				() -> assertTrue(colorTypeRepository.findColorTypeByNameAndMember(name, memberOptional.get()).isEmpty())
			);
		}

		@DisplayName("존재하지 않는 color를 삭제하려고 할 때")
		@Sql(scripts = "/sql/setup/colorType_setup.sql")
		@ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
		@ValueSource(strings = {
			"red",
			"blue",
			"black",
			"purple",
			"pink"
		})
		void removeColorIfColorNotExists(String name) {
			Optional<Member> memberOptional = memberRepository.findMemberByEmail("down@example.com");
			assertAll(
				() -> assertTrue(memberOptional.isPresent()),
				() -> assertThatThrownBy(() -> colorTypeService.removeColor(name, memberOptional.get()))
					.isInstanceOf(ColorTypeNotFoundException.class)
					.extracting(MESSAGE)
					.isEqualTo(NOT_FOUND_COLORTYPE_MEMBER_COLOR.getMessage()),

				() -> assertTrue(colorTypeRepository.findColorTypeByNameAndMember(name, memberOptional.get()).isEmpty())
			);
		}

		@DisplayName("기본으로 생성된 ColorType을 삭제하려고 하는 경우")
		@Sql(scripts = "/sql/setup/colorType_setup.sql")
		@ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
		@ValueSource(strings = {
			"ex8",
			"ex9",
			"ex10"
		})
		void removeColorIfColorTypeisDefault(String name) {
			Optional<Member> memberOptional = memberRepository.findMemberByEmail("hong@example.com");
			assertAll(
				() -> assertTrue(memberOptional.isPresent()),
				() -> assertThatThrownBy(() -> colorTypeService.removeColor(name, memberOptional.get()))
					.isInstanceOf(ColorTypeBadRequestException.class)
					.extracting(MESSAGE)
					.isEqualTo(CAN_NOT_UPDATE_OR_REMOVE_DEFAULT_COLORTYPE.getMessage())
			);
		}
	}

	static class ColorTypeRequestAggregator implements ArgumentsAggregator {
		@Override
		public Object aggregateArguments(ArgumentsAccessor argumentsAccessor,
			ParameterContext parameterContext) throws
			ArgumentsAggregationException {
			return new ColorTypeRequest(argumentsAccessor.getString(0), argumentsAccessor.getString(1));
		}
	}

	static class ColorTypeUpdateRequestAggregator implements ArgumentsAggregator {
		@Override
		public Object aggregateArguments(ArgumentsAccessor argumentsAccessor,
			ParameterContext parameterContext) throws
			ArgumentsAggregationException {
			return new ColorTypeUpdateRequest(argumentsAccessor.getString(0), argumentsAccessor.getString(1),
				argumentsAccessor.getString(2));
		}
	}
}