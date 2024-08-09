package com.fixadate.global.load.creation;

import static org.jeasy.random.FieldPredicates.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.repository.AdateJpaRepository;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.tag.entity.Tag;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import lombok.RequiredArgsConstructor;

/**
 *
 * @author yongjunhong
 * @since 2024. 6. 19.
 */

@Component
@Profile("loadtest")
@RequiredArgsConstructor
public class AdateCreation implements ApplicationRunner {
	private static final String TAG = "AdateCreation";

	private static final Logger log = LoggerFactory.getLogger(AdateCreation.class);
	private static final Random RANDOM = new Random();
	private final AdateJpaRepository adateJpaRepository;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		EasyRandom easyRandom = adate_get();

		ArrayList<Adate> adates = new ArrayList<>();
		for (int i = 0; i < 100_000; i++) {
			adates.add(easyRandom.nextObject(Adate.class));
		}
		adateJpaRepository.saveAll(adates);
	}

	public static List<Event> createEvents() {
		// FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
		// 	.plugin(new JakartaValidationPlugin())
		// 	.objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
		// 	.build();
		// var adates = fixtureMonkey.giveMeBuilder(Adate.class)
		// 	.setNull("tag")
		// 	.setNull("member")
		// 	.setNull("id")
		// 	.set("calendarId", Values.just(CombinableArbitrary.from(() -> Arbitraries.strings().sample()).unique()))
		// 	.setNotNull("*")
		// 	.sampleList(10);
		EasyRandom easyRandom = adate_get();

		ArrayList<Adate> adates = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			adates.add(easyRandom.nextObject(Adate.class));
		}

		return adates.stream()
			.map(AdateCreation::toEvent)
			.collect(Collectors.toList());
	}

	public static EasyRandom adate_get() {
		var idPredicate = named("id")
			.and(ofType(Long.class))
			.and(inClass(Adate.class));

		var memberPredicate = named("member")
			.and(ofType(Member.class))
			.and(inClass(Adate.class));

		var tagPredicate = named("tag")
			.and(ofType(Tag.class))
			.and(inClass(Adate.class));

		var calendarIdPredicate = named("calendarId")
			.and(ofType(String.class))
			.and(inClass(Adate.class));

		EasyRandomParameters parameters = new EasyRandomParameters()
			.seed(RANDOM.nextLong())
			.excludeField(idPredicate)
			.excludeField(memberPredicate)
			.excludeField(tagPredicate)
			.excludeField(calendarIdPredicate)
			.objectPoolSize(100)
			.dateRange(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31))
			.randomizationDepth(3)
			.charset(StandardCharsets.UTF_8);

		return new EasyRandom(parameters);
	}

	public static Event toEvent(Adate adate) {
		Event event = new Event();
		event.setId(adate.getCalendarId());
		event.setSummary(adate.getTitle());
		event.setDescription(adate.getNotes());
		event.setLocation(adate.getLocation());
		event.setColorId(adate.getColor());
		event.setStart(convertLocalDateTimeToEventDateTime(adate.getStartsWhen()));
		event.setEnd(convertLocalDateTimeToEventDateTime(adate.getEndsWhen()));
		event.setEtag(adate.getEtag());
		event.setReminders(new Event.Reminders().setUseDefault(false));
		event.setStatus("confirmed");
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
}
