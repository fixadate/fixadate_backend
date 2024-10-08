package com.fixadate.domain.member.repository;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.Optional;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.repository.fixture.MemberQueryRepositoryFixture;
import com.fixadate.global.config.QueryDslConfig;

@DataJpaTest
@Import({QueryDslConfig.class, MemberQueryRepository.class})
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class MemberQueryRepositoryTest extends MemberQueryRepositoryFixture {

	@Autowired
	private MemberQueryRepository memberQueryRepository;

	@Test
	void 멤버_이메일로_멤버_조회() {
		//when
		final Optional<Member> actual = memberQueryRepository.findMemberByEmail(저장된_멤버.getEmail());

		//then
		assertSoftly(softly -> {
			softly.assertThat(actual.isPresent()).isTrue();
			softly.assertThat(actual).contains(저장된_멤버);
		});
	}

	@Test
	void 멤버_아이디로_멤버_조회() {
		//when
		final Optional<Member> actual = memberQueryRepository.findMemberById(저장된_멤버.getId());

		//then
		assertSoftly(softly -> {
			softly.assertThat(actual.isPresent()).isTrue();
			softly.assertThat(actual).contains(저장된_멤버);
		});
	}

	@Test
	void 멤버_소셜_플랫폼_이메일_이름으로_멤버_조회() {
		//when
		final Optional<Member> actual = memberQueryRepository.findMemberByOauthPlatformAndEmailAndName(
			저장된_멤버.getOauthPlatform(),
			저장된_멤버.getEmail(),
			저장된_멤버.getName()
		);

		//then
		assertSoftly(softly -> {
			softly.assertThat(actual.isPresent()).isTrue();
			softly.assertThat(actual).contains(저장된_멤버);
		});
	}
}
