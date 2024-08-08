package com.fixadate.domain.member.entity;

import org.assertj.core.api.*;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import com.fixadate.domain.member.entity.fixture.MemberFixture;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MemberTest extends MemberFixture {

	@Test
	void 멤버_닉네임_수정_테스트() {
		//when
		member.updateNickname("new_nickname");
		String actual = member.getNickname();

		//then
		Assertions.assertThat(actual).isEqualTo("new_nickname");
	}

	@Test
	void 멤버_이미지_수정_테스트() {
		//when
		member.updateProfileImg("new_image");
		String actual = member.getProfileImg();

		//then
		Assertions.assertThat(actual).isEqualTo("new_image");
	}

	@Test
	void 멤버_색상_수정_테스트() {
		//when
		member.updateSignatureColor("new_color");
		String actual = member.getSignatureColor();

		//then
		Assertions.assertThat(actual).isEqualTo("new_color");
	}
}
