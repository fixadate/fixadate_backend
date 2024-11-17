package com.fixadate.integration.domain.tag.service;

import static com.fixadate.global.exception.ExceptionCode.ALREADY_EXISTS_TAG;
import static com.fixadate.global.exception.ExceptionCode.CAN_NOT_UPDATE_OR_REMOVE_DEFAULT_TAG;
import static com.fixadate.global.exception.ExceptionCode.NOT_FOUND_TAG_MEMBER_NAME;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;

import net.jqwik.api.Arbitraries;

import com.fixadate.config.DataClearExtension;
import com.fixadate.config.FixtureMonkeyConfig;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.service.repository.MemberRepository;
import com.fixadate.domain.tag.dto.request.TagRequest;
import com.fixadate.domain.tag.dto.request.TagUpdateRequest;
import com.fixadate.domain.tag.entity.Tag;
import com.fixadate.domain.tag.repository.TagRepository;
import com.fixadate.domain.tag.service.TagService;
import com.fixadate.global.exception.badrequest.TagBadRequestException;
import com.fixadate.global.exception.notfound.TagNotFoundException;

import jakarta.transaction.Transactional;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.arbitrary.CombinableArbitrary;
import com.navercorp.fixturemonkey.customizer.Values;

@ExtendWith(DataClearExtension.class)
@SpringBootTest
@Testcontainers
@Transactional
class TagServiceTest {

	@Autowired
	private TagRepository tagRepository;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private TagService tagService;

	private static final String MESSAGE = "message";

	@Nested
	@DisplayName("tag 저장 테스트")
	class RegisterTagTest {

		@DisplayName("모든 조건에 문제가 없는 경우")
		@Sql(scripts = "/sql/setup/tag_setup.sql")
		@Test
		void registerTag() {
			Optional<Member> memberOptional = memberRepository.findMemberByEmail("hong@example.com");

			FixtureMonkey fixtureMonkey = FixtureMonkeyConfig.jakartaValidationMonkey();
			var tagRequests = fixtureMonkey.giveMeBuilder(TagRequest.class)
										   .set(
											   "name",
											   Values.just(CombinableArbitrary.from(() -> Arbitraries.strings()
																									 .sample())
																			  .unique())
										   )
										   .setNotNull("color")
										   .sampleList(100);

			tagRequests.forEach(tagRequest -> {
				assertDoesNotThrow(() -> tagService.registerTag(memberOptional.get(), tagRequest));
				Optional<Tag> tagOptional = tagRepository.findTagByNameAndMember(
					tagRequest.name(), memberOptional.get());

				assertAll(
					() -> assertTrue(tagOptional.isPresent()),
					() -> assertEquals(tagRequest.name(), tagOptional.get().getName()),
					() -> assertEquals(tagRequest.color(), tagOptional.get().getColor())
				);
			});
		}

		@DisplayName("중복된 이름이 이미 있는 경우")
		@Sql(scripts = "/sql/setup/tag_setup.sql")
		@ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
		@CsvSource(value = {
			"yellow, ex1",
			"violet, ex2",
			"orange, ex3",
			"green, ex4",
			"white, ex5"
		})
		void registerTagTestIfDuplicatedColorExists(
			@AggregateWith(TagRequestAggregator.class) TagRequest tagRequest
		) {
			Optional<Member> memberOptional = memberRepository.findMemberByEmail("hong@example.com");

			assertAll(
				() -> assertThatThrownBy(() -> tagService.registerTag(memberOptional.get(), tagRequest))
					.isInstanceOf(TagBadRequestException.class)
					.extracting(MESSAGE)
					.isEqualTo(ALREADY_EXISTS_TAG.getMessage())
			);
		}
	}

	@Nested
	@DisplayName("member를 이용한 tag 조회")
	class CheckColorTest {

		@DisplayName("member가 저장한 tag이 존재하는 경우")
		@Sql(scripts = "/sql/setup/tag_setup.sql")
		@ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
		@ValueSource(strings = {"hong@example.com", "muny@example.com", "kim@example.com", "karina@example.com",
			"down@example.com"})
		void checkColorTestIfTagExists(String input) {
			Optional<Member> memberOptional = memberRepository.findMemberByEmail(input);

			assertAll(
				() -> assertTrue(memberOptional.isPresent()),
				() -> assertFalse(tagService.getTagResponses(memberOptional.get()).isEmpty())
			);
		}

		@DisplayName("member가 저장한 tag이 존재하지 않는 경우")
		@Sql(scripts = "/sql/setup/tag_setup.sql")
		@ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
		@ValueSource(strings = {"choi@example.com", "lee@example.com", "park@example.com", "cho@example.com",
			"han@example.com"})
		void checkColorTestIfTagNotExists(String input) {
			Optional<Member> memberOptional = memberRepository.findMemberByEmail(input);
			assertAll(
				() -> assertTrue(memberOptional.isPresent()),
				() -> assertEquals(0, tagService.getTagResponses(memberOptional.get()).size())
			);
		}
	}

	@Nested
	@DisplayName("updateTag 테스트")
	class UpdateTagTest {

		@DisplayName("성공적으로 업데이트를 한 경우")
		@Sql(scripts = "/sql/setup/tag_setup.sql")
		@ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
		@CsvSource(value = {
			"ex1, newColor1, newName1",
			"ex2, newColor2, newName2",
			"ex3, newColor3, newName3",
			"ex4, newColor4, newName4",
			"ex5, newColor5, newName5"
		})
		void updateColorNameIfColorExists(
			@AggregateWith(TagUpdateRequestAggregator.class) TagUpdateRequest tagUpdateRequest
		) {
			Optional<Member> memberOptional = memberRepository.findMemberByEmail("hong@example.com");
			assertDoesNotThrow(() -> tagService.updateTag(tagUpdateRequest, memberOptional.get()));
			Optional<Tag> tagOptional = tagRepository.findTagByNameAndMember(
				tagUpdateRequest.newName(), memberOptional.get());

			assertAll(
				() -> assertEquals(tagUpdateRequest.newColor(), tagOptional.get().getColor()),
				() -> assertEquals(tagUpdateRequest.newName(), tagOptional.get().getName()),
				() -> tagOptional.ifPresent(tag ->
												tag.getAdates().forEach(adate ->
																			assertEquals(
																				tagUpdateRequest.newColor(),
																				adate.getColor()
																			)))
			);

		}

		@DisplayName("성공적으로 이름만 업데이트를 한 경우")
		@Sql(scripts = "/sql/setup/tag_setup.sql")
		@ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
		@CsvSource(value = {
			"ex1,,newColor1",
			"ex2,,newColor2",
			"ex3,,newColor3",
			"ex4,,newColor4",
			"ex5,,newColor5"
		})
		void updateColorNameOnlyNameIfColorExists(
			@AggregateWith(TagUpdateRequestAggregator.class) TagUpdateRequest tagUpdateRequest
		) {
			Optional<Member> memberOptional = memberRepository.findMemberByEmail("hong@example.com");
			assertDoesNotThrow(() -> tagService.updateTag(tagUpdateRequest, memberOptional.get()));
			Optional<Tag> tagOptional = tagRepository.findTagByNameAndMember(
				tagUpdateRequest.newName(), memberOptional.get());

			assertAll(
				() -> assertTrue(tagOptional.isPresent()),
				() -> assertEquals(tagUpdateRequest.newName(), tagOptional.get().getName())
			);
		}

		@DisplayName("같은 이름으로 업데이트를 한 경우")
		@Sql(scripts = "/sql/setup/tag_setup.sql")
		@ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
		@CsvSource(value = {
			"ex1, yellow, ex1",
			"ex2, violet, ex2",
			"ex3, white, ex3",
			"ex4, orange, ex4",
			"ex5, green, ex5"
		})
		void updateColorNameToSameName(
			@AggregateWith(TagUpdateRequestAggregator.class) TagUpdateRequest tagUpdateRequest
		) {
			Optional<Member> memberOptional = memberRepository.findMemberByEmail("hong@example.com");
			assertAll(
				() -> assertThatThrownBy(
					() -> tagService.updateTag(tagUpdateRequest, memberOptional.get()))
					.isInstanceOf(TagBadRequestException.class)
					.extracting(MESSAGE)
					.isEqualTo(ALREADY_EXISTS_TAG.getMessage()),

				() -> assertTrue(tagRepository
									 .findTagByNameAndMember(tagUpdateRequest.name(), memberOptional.get()).isPresent())
			);
		}

		@DisplayName("이미 존재하는 이름으로 업데이트를 한 경우")
		@Sql(scripts = "/sql/setup/tag_setup.sql")
		@ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
		@CsvSource(value = {
			"ex1, white, ex5",
			"ex2, orange, ex5",
			"ex3, green, ex5",
			"ex4, yellow, ex5",
			"ex5, violet, ex1"
		})
		void updateColorNameToExistsColor(
			@AggregateWith(TagUpdateRequestAggregator.class) TagUpdateRequest tagUpdateRequest
		) {
			Optional<Member> memberOptional = memberRepository.findMemberByEmail("hong@example.com");

			assertAll(
				() -> assertThatThrownBy(
					() -> tagService.updateTag(tagUpdateRequest, memberOptional.get()))
					.isInstanceOf(TagBadRequestException.class)
					.extracting(MESSAGE)
					.isEqualTo(ALREADY_EXISTS_TAG.getMessage()),

				() -> assertTrue(tagRepository
									 .findTagByNameAndMember(tagUpdateRequest.name(), memberOptional.get()).isPresent())
			);
		}

		@DisplayName("기본으로 생성된 tag을 변경하려고 하는 경우")
		@Sql(scripts = "/sql/setup/tag_setup.sql")
		@ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
		@CsvSource(value = {
			"ex8, NColor, newName1",
			"ex9, NColor, newName2",
			"ex10, NColor, newName3"
		})
		void updateColorNameIfTagIsSystemDefined(
			@AggregateWith(TagUpdateRequestAggregator.class) TagUpdateRequest tagUpdateRequest
		) {
			Optional<Member> memberOptional = memberRepository.findMemberByEmail("hong@example.com");

			assertAll(
				() -> assertThatThrownBy(
					() -> tagService.updateTag(tagUpdateRequest, memberOptional.get()))
					.isInstanceOf(TagBadRequestException.class)
					.extracting(MESSAGE)
					.isEqualTo(CAN_NOT_UPDATE_OR_REMOVE_DEFAULT_TAG.getMessage())
			);
		}

		@DisplayName("color가 존재하지 않는 경우")
		@Sql(scripts = "/sql/setup/tag_setup.sql")
		@ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
		@CsvSource(value = {
			"red, newColor1, newName1",
			"blue, newColor2, newName2",
			"gray, newColor3, newName3",
			"purple, newColor4, newName4",
			"skyblue, newColor5, newName5"
		})
		void updateColorNameIfColorNotExists(
			@AggregateWith(TagUpdateRequestAggregator.class) TagUpdateRequest tagUpdateRequest
		) {
			Optional<Member> memberOptional = memberRepository.findMemberByEmail("hong@example.com");

			assertThatThrownBy(() -> tagService.updateTag(tagUpdateRequest, memberOptional.get()))
				.isInstanceOf(TagNotFoundException.class)
				.extracting(MESSAGE)
				.isEqualTo(NOT_FOUND_TAG_MEMBER_NAME.getMessage());
		}
	}

	@Nested
	@DisplayName("color 삭제 테스트")
	class RemoveColorTest {
		@DisplayName("모든 조건에 문제가 없는 경우")
		@Sql(scripts = "/sql/setup/tag_setup.sql")
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
				() -> assertDoesNotThrow(() -> tagService.removeColor(name, memberOptional.get())),
				() -> assertTrue(tagRepository.findTagByNameAndMember(name, memberOptional.get()).isEmpty())
			);
		}

		@DisplayName("존재하지 않는 color를 삭제하려고 할 때")
		@Sql(scripts = "/sql/setup/tag_setup.sql")
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
				() -> assertThatThrownBy(() -> tagService.removeColor(name, memberOptional.get()))
					.isInstanceOf(TagNotFoundException.class)
					.extracting(MESSAGE)
					.isEqualTo(NOT_FOUND_TAG_MEMBER_NAME.getMessage()),

				() -> assertTrue(tagRepository.findTagByNameAndMember(name, memberOptional.get()).isEmpty())
			);
		}

		@DisplayName("기본으로 생성된 Tag을 삭제하려고 하는 경우")
		@Sql(scripts = "/sql/setup/tag_setup.sql")
		@ParameterizedTest(name = "{index}번째 입력 값 -> {argumentsWithNames}")
		@ValueSource(strings = {
			"ex8",
			"ex9",
			"ex10"
		})
		void removeColorIfTagIsSystemDefined(String name) {
			Optional<Member> memberOptional = memberRepository.findMemberByEmail("hong@example.com");
			assertAll(
				() -> assertTrue(memberOptional.isPresent()),
				() -> assertThatThrownBy(() -> tagService.removeColor(name, memberOptional.get()))
					.isInstanceOf(TagBadRequestException.class)
					.extracting(MESSAGE)
					.isEqualTo(CAN_NOT_UPDATE_OR_REMOVE_DEFAULT_TAG.getMessage())
			);
		}
	}

	static class TagRequestAggregator implements ArgumentsAggregator {
		@Override
		public Object aggregateArguments(
			ArgumentsAccessor argumentsAccessor,
			ParameterContext parameterContext
		) throws
			ArgumentsAggregationException {
			return new TagRequest(argumentsAccessor.getString(0), argumentsAccessor.getString(1));
		}
	}

	static class TagUpdateRequestAggregator implements ArgumentsAggregator {
		@Override
		public Object aggregateArguments(
			ArgumentsAccessor argumentsAccessor,
			ParameterContext parameterContext
		) throws
			ArgumentsAggregationException {
			return new TagUpdateRequest(argumentsAccessor.getString(0), argumentsAccessor.getString(1),
										argumentsAccessor.getString(2)
			);
		}
	}
}
