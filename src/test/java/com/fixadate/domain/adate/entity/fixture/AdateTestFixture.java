package com.fixadate.domain.adate.entity.fixture;

import org.junit.jupiter.api.BeforeEach;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.tag.entity.Tag;
import com.fixadate.integration.config.FixtureMonkeyConfig;

import com.navercorp.fixturemonkey.FixtureMonkey;

@SuppressWarnings("NonAsciiCharacters")
public class AdateTestFixture {

	protected final FixtureMonkey fixtureMonkey = FixtureMonkeyConfig.jakartaValidationMonkey();
	protected Adate 일정;
	protected Adate 수정_일정;
	protected Tag 수정_태그;

	@BeforeEach
	void setUpFixture() {
		final Tag 태그 = fixtureMonkey.giveMeBuilder(Tag.class)
									.setNotNull("color")
									.sample();

		일정 = fixtureMonkey.giveMeBuilder(Adate.class)
						  .set("ifAllDay", true)
						  .set("reminders", true)
						  .set("tag", 태그)
						  .sample();

		수정_일정 = fixtureMonkey.giveMeBuilder(Adate.class)
							 .set("ifAllDay", false)
							 .set("reminders", false)
							 .sample();

		수정_태그 = fixtureMonkey.giveMeOne(Tag.class);
	}
}
