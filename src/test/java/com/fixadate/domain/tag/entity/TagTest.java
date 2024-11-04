package com.fixadate.domain.tag.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import com.fixadate.domain.tag.entity.fixture.TagFixture;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class TagTest extends TagFixture {

	@Test
	void 태그_이름_수정() {
		//when
		저장된_태그.updateTagName(새로운_이름);

		//then
		assertThat(저장된_태그.getName()).isEqualTo(새로운_이름);
	}

	@Test
	void 태그_색상_수정() {
		//when
		저장된_태그.updateTagColor(새로운_색상);

		//then
		assertThat(저장된_태그.getColor()).isEqualTo(새로운_색상);
	}

	@Test
	void 태그_삭제() {
		//when
		저장된_태그.deleteTag();

		//when
		assertSoftly(
			softly -> {
				softly.assertThat(일정_1.getTag()).isNull();
				softly.assertThat(일정_2.getTag()).isNull();
			});
	}

}
