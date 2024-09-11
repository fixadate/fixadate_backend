package com.fixadate.domain.adate.event.handler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.event.handler.fixture.AdateHandlerFixture;
import com.fixadate.domain.adate.service.AdateService;
import com.fixadate.global.util.constant.ExternalCalendar;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = AdateHandler.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class AdateHandlerTest extends AdateHandlerFixture {

	@Autowired
	private ApplicationContext applicationContext;

	@MockBean
	private AdateService adateService;

	@Nested
	@DisplayName("외부 캘린더 동기화 이벤트 테스트")
	class SetAdateEventTest {

		@Test
		void 기존_없는_일정이라면_생성한다() {
			// given
			given(adateService.getAdateByCalendarId(추가된_구글_일정.getId())).willReturn(Optional.empty());

			// when
			applicationContext.publishEvent(추가된_외부_일정_정보);

			// then
			verify(adateService, times(1)).registerExternalCalendarToAdate(
				any(Adate.class),
				any(ExternalCalendar.class)
			);
		}

		@Test
		void 일정이_취소된_경우_일정을_삭제한다() {
			// given
			given(adateService.getAdateByCalendarId(취소된_구글_일정.getId())).willReturn(Optional.of(취소된_구글_일정의_adate));

			// when
			applicationContext.publishEvent(취소된_외부_일정_정보);

			// then
			verify(adateService, times(1)).removeAdate(취소된_구글_일정의_adate);
		}

		@Test
		void 일정이_수정된_경우_기존_일정을_수정한다() {
			// given
			given(adateService.getAdateByCalendarId(수정된_구글_일정.getId())).willReturn(Optional.of(기존_일정));
			given(기존_일정.getEtag()).willReturn(adate_eTag);

			// when
			applicationContext.publishEvent(수정된_외부_일정_정보);

			// then
			verify(기존_일정, times(1)).updateFrom(any(Adate.class));
		}
	}
}
