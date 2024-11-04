package com.fixadate.domain.tag.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fixadate.domain.tag.service.fixture.TagServiceFixture;
import com.fixadate.domain.tag.service.repository.TagRepository;
import com.fixadate.global.exception.badrequest.TagBadRequestException;

@SpringBootTest
@Transactional
@Testcontainers
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TagServiceTest extends TagServiceFixture {

	private static final int REPEATED_COUNT = 100;

	@Autowired
	private TagService tagService;

	@Autowired
	private TagRepository tagRepository;

	// 저장
	@Nested
	@DisplayName("태그 저장 테스트")
	class RegisterTagTest {

		@RepeatedTest(REPEATED_COUNT)
		void 태그를_저장한다() {
			// when
			tagService.registerTag(태그_등록_요청);
			final var actual = tagRepository.findTagByNameAndMember(태그_등록_요청.tagRequest().name(), 저장된_회원);

			// then
			assertSoftly(softly -> {
				softly.assertThat(actual).isNotEmpty();
				softly.assertThat(actual.get().getName()).isEqualTo(태그_등록_요청.tagRequest().name());
				softly.assertThat(actual.get().getColor()).isEqualTo(태그_등록_요청.tagRequest().color());
			});
		}

		@RepeatedTest(REPEATED_COUNT)
		void 이미_저장된_태그_이름과_멤버인_경우_실패한다() {
			// when & then
			assertThatThrownBy(() -> tagService.registerTag(이미_저장된_태그_등록_요청))
				.isInstanceOf(TagBadRequestException.class)
				.hasMessage("변경하려고 하는 이름이 이미 존재합니다.");
		}
	}

	@Nested
	@DisplayName("태그 조회 테스트")
	class GetTagResponsesTest {

		@RepeatedTest(REPEATED_COUNT)
		void 멤버로_태그를_조회한다() {
			// when
			final var actual = tagService.getTagResponses(저장된_회원);

			// then
			assertSoftly(softly -> {
				softly.assertThat(actual.size()).isEqualTo(1);
				softly.assertThat(actual.get(0).name()).isEqualTo(저장된_태그.getName());
				softly.assertThat(actual.get(0).color()).isEqualTo(저장된_태그.getColor());
			});
		}

		@RepeatedTest(REPEATED_COUNT)
		void 멤버와_이름으로_태그를_조회한다() {
			// when
			final var actual = tagService.findTagByMemberAndColor(저장된_태그.getName(), 저장된_회원);

			// then
			assertSoftly(softly -> {
				softly.assertThat(actual.getName()).isEqualTo(저장된_태그.getName());
				softly.assertThat(actual.getColor()).isEqualTo(저장된_태그.getColor());
			});
		}
	}

	@Nested
	@DisplayName("태그 수정 테스트")
	class UpdateTagTest {

		@RepeatedTest(REPEATED_COUNT)
		void 태그를_수정한다() {
			// when
			final var actual = tagService.updateTag(태그_수정_요청);

			// then
			assertSoftly(softly -> {
				softly.assertThat(actual.name()).isEqualTo(태그_수정_요청.tagUpdateRequest().newName());
				softly.assertThat(actual.color()).isEqualTo(태그_수정_요청.tagUpdateRequest().newColor());
			});
		}

		@RepeatedTest(REPEATED_COUNT)
		void 기본_저장된_태그는_수정이_되지_않는다() {
			// when & then
			assertThatThrownBy(() -> tagService.updateTag(기본_태그_수정_요청))
				.isInstanceOf(TagBadRequestException.class)
				.hasMessage("기본으로 생성된 tag을 수정하거나 삭제할 수 없습니다.");
		}

		@RepeatedTest(REPEATED_COUNT)
		void 이미_존재하는_태그_이름으로_수정하려고_하는_경우_실패한다() {
			// when & then
			assertThatThrownBy(() -> tagService.updateTag(이름이_중복된_태그_수정_요청))
				.isInstanceOf(TagBadRequestException.class)
				.hasMessage("변경하려고 하는 이름이 이미 존재합니다.");
		}
	}

	@RepeatedTest(REPEATED_COUNT)
	void 태그를_삭제한다() {
		// when
		tagService.removeColor(저장된_태그.getName(), 저장된_회원);
		final var actual = tagRepository.findTagByNameAndMember(저장된_태그.getName(), 저장된_회원);

		// then
		assertThat(actual).isEmpty();
	}
}
