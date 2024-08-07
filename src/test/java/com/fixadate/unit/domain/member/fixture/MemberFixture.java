package com.fixadate.unit.domain.member.fixture;

import com.fixadate.domain.auth.entity.OAuthProvider;
import com.fixadate.domain.member.entity.Member;

public class MemberFixture {

	public static final Member MEMBER = Member.builder()
		.id("ex_member_id")
		.oauthId("ex_oauth_id")
		.oauthPlatform(OAuthProvider.GOOGLE)
		.name("ex_member_name")
		.nickname("ex_nickname")
		.signatureColor("ex_signature_color")
		.email("ex_email@example.com")
		.role("ROLE_USER")
		.birth("20000928")
		.build();
}
