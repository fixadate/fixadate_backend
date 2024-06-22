package com.fixadate.global.load.creation;

import static org.jeasy.random.FieldPredicates.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.repository.AdateRepository;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.tag.entity.Tag;

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

	private static final Logger log = LoggerFactory.getLogger(AdateCreation.class);
	private final AdateRepository adateRepository;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		EasyRandom easyRandom = get();

		ArrayList<Adate> adates = new ArrayList<>();
		for (int i = 0; i < 1_000; i++) {
			adates.add(easyRandom.nextObject(Adate.class));
		}
	}

	public EasyRandom get() {
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
			.seed(123L)
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
}
