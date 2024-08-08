package com.fixadate.domain.member.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.repository.fixture.MemberRepositoryFixture;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class MemberRepositoryTest extends MemberRepositoryFixture {

	@Test
	void 멤버_저장_테스트() {
		//when
		memberRepository.save(멤버);

		//then
		assertThat(멤버.getId()).isNotNull();
	}

	@Test
	void 멤버_이메일로_멤버_조회_테스트() {
		//when
		final Optional<Member> actual = memberRepository.findMemberByEmail(저장된_멤버.getEmail());

		//then
		assertThat(actual.isPresent()).isTrue();
		assertThat(actual).contains(저장된_멤버);
	}

	@Test
	void 멤버_아이디로_멤버_조회_테스트() {
		//when
		final Optional<Member> actual = memberRepository.findMemberById(저장된_멤버.getId());

		//then
		assertThat(actual.isPresent()).isTrue();
		assertThat(actual).contains(저장된_멤버);
	}

	@Test
	void 멤버_소셜_플랫폼_이메일_이름으로_멤버_조회_테스트() {
		//when
		final Optional<Member> actual = memberRepository.findMemberByOauthPlatformAndEmailAndName(
			저장된_멤버.getOauthPlatform(),
			저장된_멤버.getEmail(),
			저장된_멤버.getName()
		);

		//then
		assertThat(actual.isPresent()).isTrue();
		assertThat(actual).contains(저장된_멤버);
	}
}
