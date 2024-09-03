package com.fixadate.domain.adate.event.handler.fixture;

import static com.fixadate.global.util.constant.ConstantValue.CALENDAR_CANCELLED;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.mockito.Mock;

import net.jqwik.api.Arbitraries;

import com.fixadate.config.FixtureMonkeyConfig;
import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.event.object.ExternalCalendarSettingEvent;
import com.fixadate.domain.adate.event.object.AdateTagUpdateEvent;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.util.constant.ExternalCalendar;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import com.navercorp.fixturemonkey.FixtureMonkey;

@SuppressWarnings("NonAsciiCharacters")
public class AdateHandlerFixture {

	@Mock
	protected Adate 기존_일정;

	private final FixtureMonkey fixtureMonkey = FixtureMonkeyConfig.entityMonkey();

	private Member 회원 = fixtureMonkey.giveMeBuilder(Member.class)
									 .sample();
	private String 구글_일정_아이디 = Arbitraries.strings().alpha().ofMinLength(10).sample();
	private String 구글_일정_eTag = Arbitraries.strings().alpha().ofMinLength(10).sample();
	private String 구글_일정_summary = Arbitraries.strings().alpha().ofMinLength(10).sample();
	private String 구글_일정_descriptoin = Arbitraries.strings().alpha().ofMinLength(10).sample();
	private String 구글_일정_location = Arbitraries.strings().alpha().ofMinLength(10).sample();
	private String 구글_일정_colorId = Arbitraries.strings().alpha().ofMinLength(10).sample();
	private DateTime 일시 = new DateTime("2024-09-01T10:00:00-07:00");
	private EventDateTime 구글_일정_date = new EventDateTime().setDateTime(일시);
	private Event.Reminders 구글_일정_reminders = new Event.Reminders().setUseDefault(false);

	protected Event 추가된_구글_일정 = new Event().setId(구글_일정_아이디)
										   .setStatus("not calendar")
										   .setEtag(구글_일정_eTag)
										   .setSummary(구글_일정_summary)
										   .setDescription(구글_일정_descriptoin)
										   .setLocation(구글_일정_location)
										   .setColorId(구글_일정_colorId)
										   .setStart(구글_일정_date)
										   .setEnd(구글_일정_date)
										   .setReminders(구글_일정_reminders);
	private ExternalCalendar 구글_캘린더_타입 = ExternalCalendar.GOOGLE;
	protected ExternalCalendarSettingEvent 추가된_외부_일정_정보 = new ExternalCalendarSettingEvent(추가된_구글_일정, 회원, 구글_캘린더_타입);

	protected Event 취소된_구글_일정 = new Event().setId(구글_일정_아이디)
										   .setStatus(CALENDAR_CANCELLED.getValue());
	protected Adate 취소된_구글_일정의_adate = fixtureMonkey.giveMeBuilder(Adate.class)
													.set("calendarId", 취소된_구글_일정.getId())
													.sample();
	protected ExternalCalendarSettingEvent 취소된_외부_일정_정보 = new ExternalCalendarSettingEvent(취소된_구글_일정, 회원, 구글_캘린더_타입);

	protected String adate_eTag = Arbitraries.strings().alpha().ofMinLength(10).sample();
	protected Event 수정된_구글_일정 = 추가된_구글_일정;
	protected ExternalCalendarSettingEvent 수정된_외부_일정_정보 = new ExternalCalendarSettingEvent(수정된_구글_일정, 회원, 구글_캘린더_타입);

	protected Adate 일정 = mock(Adate.class);
	protected List<Adate> 일정들 = List.of(일정, 일정, 일정, 일정);
	protected AdateTagUpdateEvent 일정_태그_업데이트_이벤트 = new AdateTagUpdateEvent(일정들);
}
