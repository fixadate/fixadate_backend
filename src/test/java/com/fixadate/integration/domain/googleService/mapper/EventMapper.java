package com.fixadate.integration.domain.googleService.mapper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.global.auth.dto.request.MemberRegistRequest;
import com.fixadate.integration.config.FixtureMonkeyConfig;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.instantiator.Instantiator;

/**
 *
 * @author yongjunhong
 * @since 2024. 6. 6.
 */
public class EventMapper {
	private static final Random RANDOM = new Random();

	public static Event toEvent(Adate adate) {
		Event event = new Event();
		event.setSummary(adate.getTitle());
		event.setDescription(adate.getNotes());
		event.setLocation(adate.getLocation());
		event.setColorId(adate.getColor());
		event.setStart(convertLocalDateTimeToEventDateTime(adate.getStartsWhen()));
		event.setEnd(convertLocalDateTimeToEventDateTime(adate.getEndsWhen()));
		event.setEtag(adate.getEtag());
		event.setReminders(new Event.Reminders().setUseDefault(false));
		event.setStatus(RANDOM.nextBoolean() ? "cancelled" : "confirmed");
		return event;
	}

	public static EventDateTime convertLocalDateTimeToEventDateTime(LocalDateTime localDateTime) {
		if (localDateTime == null) {
			return null;
		}
		Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

		DateTime dateTime = new DateTime(date);

		EventDateTime eventDateTime = new EventDateTime();
		eventDateTime.setDateTime(dateTime);

		return eventDateTime;
	}

	public static MemberRegistRequest createRegistDto() {
		FixtureMonkey fixtureMonkey = FixtureMonkeyConfig.fieldMonkey();
		return fixtureMonkey.giveMeBuilder(MemberRegistRequest.class)
			.instantiate(Instantiator.constructor())
			.set("oauthPlatform", "google")
			.set("oauthId", "123123123")
			.set("name", "Random Name")
			.set("profileImg", "Random Profile Image")
			.set("nickname", "Random Nickname")
			.set("birth", "20000928")
			.set("gender", "Random Gender")
			.set("profession", "Random Profession")
			.set("signatureColor", "Random Color")
			.set("contentType", "Random Content Type")
			.set("email", "random@example.com")
			.set("role", "Random Role")
			.sample();
	}

	public static Adate createAdate() {
		Random random = new Random();
		Long randomId = random.nextLong();
		FixtureMonkey fixtureMonkey = FixtureMonkeyConfig.fieldMonkey();
		return fixtureMonkey.giveMeBuilder(Adate.class)
			.set("id", randomId)
			.set("title", "Random Title")
			.set("notes", "Random Notes")
			.set("location", "Random Location")
			.set("alertWhen", LocalDateTime.now())
			.set("repeatFreq", LocalDateTime.now())
			.set("color", "Random Color")
			.set("ifAllDay", true)
			.set("startsWhen", LocalDateTime.now())
			.set("endsWhen", LocalDateTime.now())
			.set("calendarId", UUID.randomUUID().toString())
			.set("etag", "Random Etag")
			.set("reminders", true)
			.sample();
	}

	public static List<Adate> createAdates() {
		List<Adate> adates = new ArrayList<>();
		for (int i = 0; i < 30; i++) {
			adates.add(createAdate());
		}
		return adates;
	}
}
