package com.fixadate.domain.adate.repository.fixture;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import com.fixadate.config.FixtureMonkeyConfig;
import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.repository.AdateJpaRepository;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.repository.MemberJpaRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.navercorp.fixturemonkey.FixtureMonkey;

@SuppressWarnings("NonAsciiCharacters")
public class AdateQueryRepositoryFixture {

	private final FixtureMonkey fixtureMonkey = FixtureMonkeyConfig.entityMonkey();

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private AdateJpaRepository adateJpaRepository;

	@Autowired
	private MemberJpaRepository memberRepository;

	protected LocalDateTime 범위_시작일 = LocalDateTime.now().minusDays(5);
	protected LocalDateTime 범위_종료일 = LocalDateTime.now().plusDays(5);
	protected LocalDateTime 일정이_없는_범위_시작일 = LocalDateTime.now().plusDays(10);
	protected LocalDateTime 일정이_없는_범위_종료일 = 일정이_없는_범위_시작일.plusDays(10);

	protected Member 회원;
	protected Adate 일정;
	protected Adate 범위_내_일정;
	protected Adate 범위에_걸치는_일정;
	protected Adate 범위_밖의_일정;
	protected String 일정이_없는_캘린더_아이디 = "none adate";

	@BeforeEach
	void setUpFixture() {
		회원 = fixtureMonkey.giveMeBuilder(Member.class)
						  .setNull("id")
						  .setNotNull("oauthId")
						  .setNotNull("name")
						  .setNotNull("email")
						  .setNotNull("oauthPlatform")
						  .setNotNull("signatureColor")
						  .setNotNull("nickname")
						  .setNull("googleCredentials")
						  .setNull("pushKey")
						  .sample();

		memberRepository.save(회원);

		일정 = fixtureMonkey.giveMeBuilder(Adate.class)
						  .setNull("id")
						  .set("startsWhen", 범위_시작일)
						  .set("endsWhen", 범위_종료일)
						  .setNotNull("calendarId")
						  .set("member", 회원)
						  .setNull("tag")
						  .sample();
		범위_내_일정 = 일정;
		범위에_걸치는_일정 = fixtureMonkey.giveMeBuilder(Adate.class)
								  .setNull("id")
								  .set("startsWhen", 범위_시작일.plusDays(1))
								  .set("endsWhen", 범위_종료일.plusDays(5))
								  .setNotNull("calendarId")
								  .set("member", 회원)
								  .setNull("tag")
								  .sample();
		범위_밖의_일정 = fixtureMonkey.giveMeBuilder(Adate.class)
								.setNull("id")
								.set("startsWhen", 범위_시작일.minusDays(5))
								.set("endsWhen", 범위_시작일.minusDays(1))
								.setNotNull("calendarId")
								.set("member", 회원)
								.setNull("tag")
								.sample();

		adateJpaRepository.saveAll(List.of(범위_내_일정, 범위에_걸치는_일정, 범위_밖의_일정));

		em.flush();
		em.clear();
	}
}
