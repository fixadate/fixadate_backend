package com.fixadate.domain.adate.entity.fixture;

import org.junit.jupiter.api.BeforeEach;

import com.fixadate.config.FixtureMonkeyConfig;
import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.tag.entity.Tag;

import com.navercorp.fixturemonkey.FixtureMonkey;

@SuppressWarnings("NonAsciiCharacters")
public class AdateFixture {

	protected final FixtureMonkey fixtureMonkey = FixtureMonkeyConfig.entityMonkey();
	protected Member 일정_주인;
	protected Member 다른_회원;
	protected Adate 일정;
	protected Adate 수정_일정;
	protected Tag 수정_태그;

	@BeforeEach
	void setUpFixture() {
		일정_주인 = fixtureMonkey.giveMeBuilder(Member.class)
							 .set("id", "1")
							 .sample();

		다른_회원 = fixtureMonkey.giveMeBuilder(Member.class)
							 .set("id", "2")
							 .sample();

		final Tag 태그 = fixtureMonkey.giveMeBuilder(Tag.class)
									.setNotNull("color")
									.sample();

		일정 = fixtureMonkey.giveMeBuilder(Adate.class)
						  .set("ifAllDay", true)
						  .set("reminders", true)
						  .set("tag", 태그)
						  .set("member", 일정_주인)
						  .sample();

		수정_일정 = fixtureMonkey.giveMeBuilder(Adate.class)
							 .set("ifAllDay", false)
							 .set("reminders", false)
							 .sample();

		수정_태그 = fixtureMonkey.giveMeOne(Tag.class);
	}
}
