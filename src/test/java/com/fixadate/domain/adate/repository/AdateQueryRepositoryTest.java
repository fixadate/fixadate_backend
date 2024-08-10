package com.fixadate.domain.adate.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.repository.fixture.AdateQueryRepositoryFixture;
import com.fixadate.global.config.QueryDslConfig;
import com.querydsl.jpa.impl.JPAQueryFactory;

@DataJpaTest
@Testcontainers
@Import(QueryDslConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class AdateQueryRepositoryTest extends AdateQueryRepositoryFixture {

	private AdateQueryRepository adateQueryRepository;

	@BeforeEach
	void setUp(@Autowired final JPAQueryFactory jpaQueryFactory) {
		adateQueryRepository = new AdateQueryRepository(jpaQueryFactory);
	}

	@Nested
	@DisplayName("회원과 날짜 범위에 따라 일정 목록 조회 테스트")
	class FindByDateRangeTest {

		@Test
		void 특정_날짜_범위에_회원의_일정이_목록을_조회한다() {
			// when
			final List<Adate> actual = adateQueryRepository.findByDateRange(회원, 범위_시작일, 범위_종료일);

			// then
			assertSoftly(softly -> {
				softly.assertThat(actual).hasSize(2);
				softly.assertThat(actual).containsExactly(범위_내_일정, 범위에_걸치는_일정);
				softly.assertThat(actual).doesNotContain(범위_밖의_일정);
			});
		}

		@Test
		void 특정_날짜_범위에_회원의_일정이_없다면_빈_배열을_반환한다() {
			// when
			final List<Adate> actual = adateQueryRepository.findByDateRange(회원, 일정이_없는_범위_시작일, 일정이_없는_범위_종료일);

			// then
			assertThat(actual).hasSize(0);
		}
	}
}
