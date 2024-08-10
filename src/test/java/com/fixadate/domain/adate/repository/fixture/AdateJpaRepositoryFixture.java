package com.fixadate.domain.adate.repository.fixture;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.repository.AdateJpaRepository;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.repository.MemberRepository;
import com.fixadate.integration.config.FixtureMonkeyConfig;

import com.navercorp.fixturemonkey.FixtureMonkey;

@SuppressWarnings("NonAsciiCharacters")
public class AdateJpaRepositoryFixture {

	private final FixtureMonkey fixtureMonkey = FixtureMonkeyConfig.entityMonkey();

	@Autowired
	private AdateJpaRepository adateJpaRepository;

	@Autowired
	private MemberRepository memberRepository;

	protected Adate 일정;
	protected String 캘린더_아이디;
	protected String 일정이_없는_캘린더_아이디 = "none adate";

	@BeforeEach
	void setUpFixture() {
		final Member 회원 = fixtureMonkey.giveMeBuilder(Member.class)
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
						  .setNotNull("calendarId")
						  .set("member", 회원)
						  .setNull("tag")
						  .sample();

		adateJpaRepository.save(일정);

		캘린더_아이디 = 일정.getCalendarId();
	}
}
