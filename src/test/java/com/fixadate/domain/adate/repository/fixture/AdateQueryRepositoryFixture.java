package com.fixadate.domain.adate.repository.fixture;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.repository.AdateJpaRepository;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.repository.MemberRepository;
import com.fixadate.config.FixtureMonkeyConfig;

import com.navercorp.fixturemonkey.FixtureMonkey;

@SuppressWarnings("NonAsciiCharacters")
public class AdateQueryRepositoryFixture {

	private final FixtureMonkey fixtureMonkey = FixtureMonkeyConfig.entityMonkey();

	@Autowired
	private AdateJpaRepository adateJpaRepository;

	@Autowired
	private MemberRepository memberRepository;

	protected LocalDateTime 범위_시작일 = LocalDateTime.now().minusDays(5);
	protected LocalDateTime 범위_종료일 = LocalDateTime.now().plusDays(5);
	protected LocalDateTime 일정이_없는_범위_시작일 = LocalDateTime.now().plusDays(10);
	protected LocalDateTime 일정이_없는_범위_종료일 = 일정이_없는_범위_시작일.plusDays(10);

	protected Member 회원;
	protected Adate 범위_내_일정;
	protected Adate 범위에_걸치는_일정;
	protected Adate 범위_밖의_일정;

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

		범위_내_일정 = fixtureMonkey.giveMeBuilder(Adate.class)
							   .setNull("id")
							   .set("startsWhen", 범위_시작일)
							   .set("endsWhen", 범위_종료일)
							   .setNotNull("calendarId")
							   .set("member", 회원)
							   .setNull("tag")
							   .sample();
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

		// TODO: [질문] jpa repository를 사용해도 괜찮을까요? 만약, jpa를 사용하지 않게 되면 수정 범위가 여기까지로 확장됩니다.
		adateJpaRepository.saveAll(List.of(범위_내_일정, 범위에_걸치는_일정, 범위_밖의_일정));
	}
}
