package com.fixadate.unit.domain.tag.fixture;

import java.util.Collections;
import java.util.List;

import com.fixadate.domain.tag.entity.Tag;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.auth.entity.OAuthProvider;

public class TagFixture {

	public static final Tag TAG = Tag.builder()
		.color("red")
		.name("ex_name")
		.member(Member.builder()
			.id("ex_member_id")
			.oauthId("ex_oauth_id")
			.oauthPlatform(OAuthProvider.GOOGLE)
			.name("ex_member_name")
			.nickname("ex_nickname")
			.signatureColor("ex_signature_color")
			.email("ex_email@example.com")
			.role("ROLE_USER")
			.build())
		.adates(Collections.emptyList())
		.build();

	public static final Tag TAG1 = Tag.builder()
		.color("blue")
		.name("ex_name")
		.member(Member.builder()
			.id("ex_member_id")
			.oauthId("ex_oauth_id")
			.oauthPlatform(OAuthProvider.GOOGLE)
			.name("ex_member_name")
			.nickname("ex_nickname")
			.signatureColor("ex_signature_color")
			.email("ex_email@example.com")
			.role("ROLE_USER")
			.build())
		.adates(Collections.emptyList())
		.build();

	public static final Tag TAG2 = Tag.builder()
		.color("green")
		.name("ex_name")
		.member(Member.builder()
			.id("ex_member_id")
			.oauthId("ex_oauth_id")
			.oauthPlatform(OAuthProvider.GOOGLE)
			.name("ex_member_name")
			.nickname("ex_nickname")
			.signatureColor("ex_signature_color")
			.email("ex_email@example.com")
			.role("ROLE_USER")
			.build())
		.adates(Collections.emptyList())
		.build();

	public static final List<Tag> TAGS = List.of(TAG1, TAG2);
}
