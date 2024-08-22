package com.fixadate.domain.member.service;

import static com.fixadate.global.exception.ExceptionCode.NOT_FOUND_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fixadate.domain.member.dto.response.MemberInfoDto;
import com.fixadate.domain.member.service.fixture.MemberServiceFixture;
import com.fixadate.global.exception.notfound.MemberNotFoundException;

import software.amazon.awssdk.services.s3.S3Client;

@SpringBootTest
@Transactional
@Testcontainers
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MemberServiceTest extends MemberServiceFixture {

	private static final int REPEATED_COUNT = 100;

	@Autowired
	private MemberService memberService;

	@Autowired
	private S3Client s3Client;

	@RepeatedTest(REPEATED_COUNT)
	void 멤버_닉네임_생성() {
		// when
		final String actual = memberService.generateRandomNickname();

		// then
		assertSoftly(softly -> {
			softly.assertThat(actual).isNotNull();
			softly.assertThat(actual.length()).isGreaterThan(4);
		});
	}

	@DisplayName("멤버 정보 조회")
	@Nested
	class GetMemberInfoTest {

		@RepeatedTest(REPEATED_COUNT)
		void 멤버_정보_조회() {
			// when
			final MemberInfoDto actual = memberService.getMemberInfo(멤버_아이디);

			// then
			assertSoftly(softly -> {
				softly.assertThat(actual).isNotNull();
				softly.assertThat(actual.name()).isEqualTo(멤버.getName());
				softly.assertThat(actual.nickname()).isEqualTo(멤버.getNickname());
				softly.assertThat(actual.birth()).isEqualTo(멤버.getBirth());
				softly.assertThat(actual.gender()).isEqualTo(멤버.getGender());
				softly.assertThat(actual.signatureColor()).isEqualTo(멤버.getSignatureColor());
				softly.assertThat(actual.profession()).isEqualTo(멤버.getProfession());
				softly.assertThat(actual.url()).isNotBlank();
			});
		}

		@RepeatedTest(REPEATED_COUNT)
		void 멤버가_없는_경우_404_반환() {
			// given
			final String memberId = 멤버_아이디 + "extra";

			// when & then
			assertThatThrownBy(() -> memberService.getMemberInfo(memberId))
				.isInstanceOf(MemberNotFoundException.class)
				.hasMessage(NOT_FOUND_MEMBER_ID.getMessage());
		}
	}

	@DisplayName("멤버 정보 수정")
	@Nested
	class UpdateMemberInfoTest {

		@RepeatedTest(REPEATED_COUNT)
		void 멤버_정보_수정() {
			// given
			s3Client.putObject(입력_요청, 입력_바디);

			// when
			final MemberInfoDto actual = memberService.updateMemberInfo(멤버_정보_수정_요청);

			// then
			assertSoftly(softly -> {
				softly.assertThat(actual).isNotNull();
				softly.assertThat(actual.name()).isEqualTo(멤버.getName());
				softly.assertThat(actual.nickname()).isEqualTo(멤버_정보_수정_요청.nickname());
				softly.assertThat(actual.birth()).isEqualTo(멤버.getBirth());
				softly.assertThat(actual.gender()).isEqualTo(멤버.getGender());
				softly.assertThat(actual.signatureColor()).isEqualTo(멤버_정보_수정_요청.signatureColor());
				softly.assertThat(actual.profession()).isEqualTo(멤버_정보_수정_요청.profession());
				softly.assertThat(actual.url()).isNotBlank();
			});
		}

		@RepeatedTest(REPEATED_COUNT)
		void 이미지를_변경하지_않는_경우_Url_null_반환() {
			// when
			final MemberInfoDto actual = memberService.updateMemberInfo(멤버_정보_수정_요청_이미지_없는_경우);

			// then
			assertSoftly(softly -> {
				softly.assertThat(actual).isNotNull();
				softly.assertThat(actual.url()).isNull();
			});
		}
	}
}
