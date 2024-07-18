package com.fixadate.unit.domain.pushkey.fixture;

import com.fixadate.domain.pushkey.entity.PushKey;

public class PushKeyFixture {

	public static final PushKey PUSH_KEY = PushKey.builder()
		.pushKey("example")
		.memberId("1")
		.build();
}
