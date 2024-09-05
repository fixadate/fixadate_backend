package com.fixadate.domain.adate.entity;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import com.fixadate.domain.adate.entity.fixture.AdateFixture;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class AdateTest extends AdateFixture {

	@Test
	void 일정의_주인이라면_참을_반환한다() {
		// when
		final boolean actual = 일정.isOwner(일정_주인);

		// then
		assertThat(actual).isTrue();
	}

	@Test
	void 일정의_주인이_아니라면_거짓을_반환한다() {
		// when
		final boolean actual = 일정.isOwner(다른_회원);

		// then
		assertThat(actual).isFalse();
	}

	@Test
	void 일정의_태그와_색상_삭제_테스트() {
		// when
		일정.removeTagAndColor();

		// then
		assertSoftly(softly -> {
			softly.assertThat(일정.getTag()).isNull();
			softly.assertThat(일정.getColor()).isNull();
		});
	}

	@Test
	void 일정을_통한_정보_수정_테스트() {
		// when
		일정.updateFrom(수정_일정);

		// then
		assertSoftly(softly -> {
			softly.assertThat(일정.getTitle()).isEqualTo(수정_일정.getTitle());
			softly.assertThat(일정.getNotes()).isEqualTo(수정_일정.getNotes());
			softly.assertThat(일정.getLocation()).isEqualTo(수정_일정.getLocation());
			softly.assertThat(일정.getColor()).isEqualTo(수정_일정.getColor());
			softly.assertThat(일정.getStartsWhen()).isEqualTo(수정_일정.getStartsWhen());
			softly.assertThat(일정.getEndsWhen()).isEqualTo(수정_일정.getEndsWhen());
			softly.assertThat(일정.getCalendarId()).isEqualTo(수정_일정.getCalendarId());
			softly.assertThat(일정.getEtag()).isEqualTo(수정_일정.getEtag());
			softly.assertThat(일정.isReminders()).isEqualTo(수정_일정.isReminders());
		});
	}

	@Test
	void 일정_제목_수정_테스트() {
		// given
		final String newTitle = "new title";

		// when
		일정.updateTitle(newTitle);

		// then
		assertThat(일정.getTitle()).isEqualTo(newTitle);
	}

	@Test
	void 일정의_노트_수정_테스트() {
		// given
		final String newNotes = "new notes";

		// when
		일정.updateNotes(newNotes);

		// then
		assertThat(일정.getNotes()).isEqualTo(newNotes);
	}

	@Test
	void 일정의_위치_수정_테스트() {
		// given
		final String newLocation = "new location";

		// when
		일정.updateLocation(newLocation);

		// then
		assertThat(일정.getLocation()).isEqualTo(newLocation);
	}

	@Test
	void 일정의_알림_시간_수정_테스트() {
		// given
		final LocalDateTime newAlertWhen = LocalDateTime.now();

		// when
		일정.updateAlertWhen(newAlertWhen);

		// then
		assertThat(일정.getAlertWhen()).isEqualTo(newAlertWhen);
	}

	@Test
	void 일정의_반복_날짜_수정_테스트() {
		// given
		final LocalDateTime newRepeatFreq = LocalDateTime.now();

		// when
		일정.updateRepeatFreq(newRepeatFreq);

		// then
		assertThat(일정.getRepeatFreq()).isEqualTo(newRepeatFreq);
	}

	@Test
	void 일정의_종일_여부_수정_테스트() {
		// when
		일정.updateIfAllDay(false);

		// then
		assertThat(일정.isIfAllDay()).isFalse();
	}

	@Test
	void 일정의_시작_날짜_수정_테스트() {
		// given
		final LocalDateTime newStartsWhen = LocalDateTime.now();

		// when
		일정.updateStartsWhen(newStartsWhen);

		// then
		assertThat(일정.getStartsWhen()).isEqualTo(newStartsWhen);
	}

	@Test
	void 일정의_종료_날짜_수정_테스트() {
		// given
		final LocalDateTime newEndsWhen = LocalDateTime.now();

		// when
		일정.updateEndsWhen(newEndsWhen);

		// then
		assertThat(일정.getEndsWhen()).isEqualTo(newEndsWhen);
	}

	@Test
	void 일정의_리마인드_여부_수정_테스트() {
		// when
		일정.updateReminders(false);

		// then
		assertThat(일정.isReminders()).isFalse();
	}

	@Test
	void 일정의_태그_색상_수정_테스트() {
		// when
		일정.updateTag(수정_태그);

		// then
		assertSoftly(softly -> {
			softly.assertThat(일정.getTag()).isEqualTo(수정_태그);
			softly.assertThat(일정.getColor()).isEqualTo(수정_태그.getColor());
		});
	}

	@Test
	void 일정의_색상_수정_테스트() {
		// when
		일정.refreshColorFromCurrentTag();

		// then
		assertThat(일정.getColor()).isEqualTo(일정.getTag().getColor());
	}
}
