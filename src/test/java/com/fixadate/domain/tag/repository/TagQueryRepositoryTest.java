package com.fixadate.domain.tag.repository;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fixadate.domain.tag.entity.Tag;
import com.fixadate.domain.tag.repository.fixture.TagQueryRepositoryFixture;
import com.fixadate.global.config.QueryDslConfig;

@DataJpaTest
@Import({QueryDslConfig.class, TagQueryRepository.class})
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class TagQueryRepositoryTest extends TagQueryRepositoryFixture {

	@Autowired
	private TagQueryRepository tagQueryRepository;

	@Test
	void 태그_이름과_회원으로_태그_조회() {
		//when
		final Optional<Tag> actual = tagQueryRepository.findTagByNameAndMember(저장된_태그.getName(), 저장된_회원);

		//then
		assertSoftly(softly -> {
			softly.assertThat(actual.isPresent()).isTrue();
			softly.assertThat(actual).contains(저장된_태그);
		});
	}

	@Test
	void 회원으로_태그_조회() {
		//when
		final List<Tag> actual = tagQueryRepository.findTagsByMember(저장된_회원);

		//then
		assertSoftly(softly -> {
			softly.assertThat(actual.size()).isEqualTo(1);
			softly.assertThat(actual)
				  .usingElementComparator(Comparator.comparing(Tag::getName)
													.thenComparing(Tag::getColor)
													.thenComparing(Tag::getName))
				  .containsExactlyInAnyOrderElementsOf(List.of(저장된_태그));
		});
	}
}
