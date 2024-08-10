package com.fixadate.domain.adate.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.Optional;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.repository.fixture.AdateJpaRepositoryFixture;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class AdateJpaRepositoryTest extends AdateJpaRepositoryFixture {

	@Autowired
	private AdateJpaRepository adateJpaRepository;

	@Nested
	class FindAdateByCalendarIdTest {

		@Test
		void 캘린더_아이디를_통해_일정을_조회한다() {
			// when
			final Optional<Adate> actual = adateJpaRepository.findAdateByCalendarId(캘린더_아이디);

			// then
			assertSoftly(softly -> {
				softly.assertThat(actual).isPresent();
				softly.assertThat(actual).contains(일정);
			});
		}

		@Test
		void 캘린더_아이디에_해당하는_일정이_없다면_빈값을_조회한다() {
			// when
			final Optional<Adate> actual = adateJpaRepository.findAdateByCalendarId(일정이_없는_캘린더_아이디);

			// then
			assertThat(actual).isEmpty();
		}
	}
}
