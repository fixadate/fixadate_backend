package com.fixadate.domain.tag.entity.fixture;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;

import com.fixadate.config.FixtureMonkeyConfig;
import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.tag.entity.Tag;

import com.navercorp.fixturemonkey.FixtureMonkey;

@SuppressWarnings("NonAsciiCharacters")
public class TagFixture {

	protected final FixtureMonkey fixtureMonkey = FixtureMonkeyConfig.entityMonkey();
	protected Tag 저장된_태그;
	protected Adate 일정_1;
	protected Adate 일정_2;
	protected String 새로운_이름;
	protected String 새로운_색상;

	@BeforeEach
	void setUpFixture() {
		일정_1 = fixtureMonkey.giveMeBuilder(Adate.class)
							.set("ifAllDay", false)
							.set("reminders", false)
							.set("tag", 저장된_태그)
							.sample();

		일정_2 = fixtureMonkey.giveMeBuilder(Adate.class)
							.set("ifAllDay", false)
							.set("reminders", false)
							.set("tag", 저장된_태그)
							.sample();


		저장된_태그 = fixtureMonkey.giveMeBuilder(Tag.class)
							  .setNotNull("name")
							  .setNotNull("color")
							  .setNotNull("systemDefined")
							  .set("adates", List.of(일정_1, 일정_2))
							  .setNull("member")
							  .sample();

		새로운_이름 = "newName123";
		새로운_색상 = "newColor123";
	}
}
