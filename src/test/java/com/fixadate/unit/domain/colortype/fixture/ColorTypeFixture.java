package com.fixadate.unit.domain.colortype.fixture;

import java.util.Collections;
import java.util.List;

import com.fixadate.domain.colortype.entity.ColorType;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.auth.entity.OAuthProvider;

public class ColorTypeFixture {

	public static final ColorType COLOR_TYPE = ColorType.builder()
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

	public static final ColorType COLOR_TYPE1 = ColorType.builder()
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

	public static final ColorType COLOR_TYPE2 = ColorType.builder()
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

	public static final List<ColorType> COLOR_TYPES = List.of(COLOR_TYPE1, COLOR_TYPE2);
}
