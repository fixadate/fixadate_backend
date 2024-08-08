package com.fixadate.domain.member.entity.fixture;

import org.junit.jupiter.api.BeforeEach;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.integration.config.FixtureMonkeyConfig;

import com.navercorp.fixturemonkey.FixtureMonkey;

public class MemberFixture {

	protected Member member;
	protected FixtureMonkey fixtureMonkey = FixtureMonkeyConfig.entityMonkey();

	@BeforeEach
	void setUp() {
		member = fixtureMonkey.giveMeOne(Member.class);
	}
}
