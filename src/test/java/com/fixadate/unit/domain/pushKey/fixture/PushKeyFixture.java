package com.fixadate.unit.domain.pushKey.fixture;

import com.fixadate.domain.pushKey.entity.PushKey;

public class PushKeyFixture {

	public static final PushKey PUSH_KEY = PushKey.builder()
		.pushKey("example")
		.memberId("1")
		.build();
}
