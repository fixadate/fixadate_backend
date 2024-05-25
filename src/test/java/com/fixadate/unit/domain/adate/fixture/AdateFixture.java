package com.fixadate.unit.domain.adate.fixture;

import java.time.LocalDateTime;
import java.util.List;

import com.fixadate.domain.adate.entity.Adate;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

public class AdateFixture {

	public static final Adate ADATE = Adate.builder()
		.title("ex_title")
		.notes("ex_notes")
		.location("ex_location")
		.alertWhen(LocalDateTime.now())
		.repeatFreq(LocalDateTime.now())
		.color("ex_color")
		.ifAllDay(true)
		.startsWhen(LocalDateTime.now())
		.endsWhen(LocalDateTime.now())
		.calendarId("ex_calendar_id")
		.etag("ex_etag")
		.reminders(true)
		.build();

	public static final Adate ADATE1 = Adate.builder()
		.title("ex_title")
		.notes("ex_notes")
		.location("ex_location")
		.alertWhen(LocalDateTime.now())
		.repeatFreq(LocalDateTime.now())
		.color("ex_color")
		.ifAllDay(true)
		.startsWhen(LocalDateTime.now())
		.endsWhen(LocalDateTime.now())
		.calendarId("ex_calendar_id")
		.etag("ex_etag")
		.reminders(true)
		.build();

	public static final Adate ADATE2 = Adate.builder()
		.title("ex_title")
		.notes("ex_notes")
		.location("ex_location")
		.alertWhen(LocalDateTime.now())
		.repeatFreq(LocalDateTime.now())
		.color("ex_color")
		.ifAllDay(true)
		.startsWhen(LocalDateTime.now())
		.endsWhen(LocalDateTime.now())
		.calendarId("ex_calendar_id")
		.etag("ex_etag")
		.reminders(true)
		.build();

	public static final List<Adate> ADATES = List.of(ADATE1, ADATE2);
	public static final Event EVENT1 = new Event();
	public static final Event EVENT2 = new Event();
	public static final EventDateTime EVENT_DATE_TIME = new EventDateTime();
}
