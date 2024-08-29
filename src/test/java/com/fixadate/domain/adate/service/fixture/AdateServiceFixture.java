package com.fixadate.domain.adate.service.fixture;

import static com.fixadate.global.util.constant.ConstantValue.ADATE_WITH_COLON;
import static com.fixadate.global.util.constant.ConstantValue.GOOGLE_CALENDAR;
import static com.fixadate.global.util.constant.ConstantValue.GOOGLE_CALENDAR_COLOR;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.fixadate.config.FixtureMonkeyConfig;
import com.fixadate.domain.adate.dto.AdateRegisterDto;
import com.fixadate.domain.adate.dto.AdateUpdateDto;
import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.service.repository.AdateRepository;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.service.repository.MemberRepository;
import com.fixadate.domain.tag.entity.Tag;
import com.fixadate.domain.tag.repository.TagRepository;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.customizer.Values;

@SuppressWarnings("NonAsciiCharacters")
public class AdateServiceFixture {

	private final FixtureMonkey recordFixtureMonkey = FixtureMonkeyConfig.jakartaValidationMonkey();
	private final FixtureMonkey entityFixtureMonkey = FixtureMonkeyConfig.entityMonkey();

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private TagRepository tagRepository;

	@Autowired
	private AdateRepository adateRepository;

	@Autowired
	private RedisTemplate<Object, Object> redisJsonTemplate;

	protected AdateRegisterDto 일정_저장_요청;
	protected AdateRegisterDto 태그가_있는_일정_저장_요청;
	protected AdateRegisterDto 태그가_없는_일정_저장_요청;
	protected AdateRegisterDto 존재하지_않는_태그를_통해_일정_저장_요청;
	protected Member 회원;
	protected Adate 저장된_일정;
	protected Adate 삭제된_일정;
	protected Adate 외부_캘린더_일정;
	protected Adate 일정1;
	protected Adate 일정2;
	protected Adate 태그가_있는_일정;
	protected String 존재하지_않는_일정의_캘린더_아이디 = "not exist calendar Id";
	protected LocalDateTime 시작_일시;
	protected LocalDateTime 종료_일시;
	protected LocalDateTime 시작_날짜보다_빠른_종료_일시;
	protected LocalDateTime 일정_없는_시작_일시 = LocalDateTime.now().minusYears(10);
	protected LocalDateTime 일정_없는_종료_일시 = 일정_없는_시작_일시.plusDays(5);
	protected AdateUpdateDto 전체_수정_요청;
	protected AdateUpdateDto 태그_수정_요청;
	protected AdateUpdateDto 제목_수정_요청;
	protected AdateUpdateDto 노트_수정_요청;
	protected AdateUpdateDto 위치_수정_요청;
	protected AdateUpdateDto 알람_시간_수정_요청;
	protected AdateUpdateDto 반복_시간_수정_요청;

	@BeforeEach
	void setUpFixture() {
		final Values.Just uniqueStringOption = FixtureMonkeyConfig.uniqueStringOption(10);
		회원 = entityFixtureMonkey.giveMeBuilder(Member.class)
								.setNull("id")
								.set("oauthId", uniqueStringOption)
								.setNotNull("name")
								.setNotNull("email")
								.setNotNull("oauthPlatform")
								.setNotNull("signatureColor")
								.setNotNull("nickname")
								.setNull("googleCredentials")
								.setNull("pushKey")
								.sample();

		memberRepository.save(회원);

		final Tag 태그 = entityFixtureMonkey.giveMeBuilder(Tag.class)
										  .setNull("id")
										  .setNotNull("color")
										  .set("name", uniqueStringOption)
										  .set("member", 회원)
										  .sample();
		final Tag 구글_캘린더_태그 = entityFixtureMonkey.giveMeBuilder(Tag.class)
												 .setNull("id")
												 .set("color", GOOGLE_CALENDAR_COLOR.getValue())
												 .set("name", GOOGLE_CALENDAR.getValue())
												 .set("member", 회원)
												 .sample();

		tagRepository.saveAll(List.of(태그, 구글_캘린더_태그));

		final LocalDateTime now = LocalDateTime.now();
		태그가_있는_일정_저장_요청 = recordFixtureMonkey.giveMeBuilder(AdateRegisterDto.class)
											 .setNotNull("title")
											 .set("tagName", 태그.getName())
											 .set("startsWhen", now)
											 .set("endsWhen", now.plusDays(5))
											 .sample();
		태그가_없는_일정_저장_요청 = recordFixtureMonkey.giveMeBuilder(AdateRegisterDto.class)
											 .setNotNull("title")
											 .setNull("tagName")
											 .set("startsWhen", now)
											 .set("endsWhen", now.plusDays(5))
											 .sample();
		일정_저장_요청 = 태그가_없는_일정_저장_요청;
		존재하지_않는_태그를_통해_일정_저장_요청 = recordFixtureMonkey.giveMeBuilder(AdateRegisterDto.class)
													 .setNotNull("title")
													 .set("tagName", "not exist tag name")
													 .set("startsWhen", now)
													 .set("endsWhen", now.plusDays(5))
													 .sample();

		저장된_일정 = entityFixtureMonkey.giveMeBuilder(Adate.class)
									.setNull("id")
									.set("calendarId", uniqueStringOption)
									.set("startsWhen", now)
									.set("endsWhen", now.plusDays(5))
									.set("member", 회원)
									.setNull("tag")
									.sample();
		일정1 = 저장된_일정;
		final LocalDateTime 일정2_시작일 = now.plusMonths(2);
		일정2 = entityFixtureMonkey.giveMeBuilder(Adate.class)
								 .setNull("id")
								 .set("calendarId", uniqueStringOption)
								 .set("startsWhen", 일정2_시작일)
								 .set("endsWhen", 일정2_시작일.plusDays(5))
								 .set("member", 회원)
								 .setNull("tag")
								 .sample();

		시작_일시 = 일정1.getStartsWhen();
		종료_일시 = 일정2.getEndsWhen();
		시작_날짜보다_빠른_종료_일시 = 시작_일시.minusDays(10);

		// TODO: [추후] ifAllday의 타입이 참조/원시 결정 사항에 따라 수정 필요
		삭제된_일정 = entityFixtureMonkey.giveMeBuilder(Adate.class)
									.setNull("id")
									.set("calendarId", uniqueStringOption)
									.setNotNull("ifAllDay")
									.set("member", 회원)
									.setNull("tag")
									.sample();

		외부_캘린더_일정 = entityFixtureMonkey.giveMeBuilder(Adate.class)
									   .setNull("id")
									   .set("calendarId", uniqueStringOption)
									   .setNotNull("ifAllDay")
									   .set("startsWhen", now)
									   .set("endsWhen", now.plusDays(5))
									   .set("member", 회원)
									   .setNull("tag")
									   .sample();

		태그가_있는_일정 = entityFixtureMonkey.giveMeBuilder(Adate.class)
									   .setNull("id")
									   .set("calendarId", uniqueStringOption)
									   .set("startsWhen", 일정2_시작일.plusDays(10))
									   .set("endsWhen", 일정2_시작일.plusDays(15))
									   .set("member", 회원)
									   .set("tag", 태그)
									   .sample();

		adateRepository.save(저장된_일정);
		adateRepository.save(일정2);
		adateRepository.save(삭제된_일정);
		adateRepository.delete(삭제된_일정);
		adateRepository.save(태그가_있는_일정);

		final String key = ADATE_WITH_COLON.getValue() + 삭제된_일정.getCalendarId();
		redisJsonTemplate.opsForValue().set(key, 삭제된_일정, Duration.ofDays(1));

		전체_수정_요청 = recordFixtureMonkey.giveMeBuilder(AdateUpdateDto.class)
									  .setNotNull("title")
									  .setNotNull("notes")
									  .setNotNull("location")
									  .setNotNull("alertWhen")
									  .setNotNull("repeatFreq")
									  .set("tagName", 구글_캘린더_태그.getName())
									  .setNotNull("ifAllDay")
									  .setNotNull("startsWhen")
									  .setNotNull("endsWhen")
									  .setNotNull("reminders")
									  .sample();
		태그_수정_요청 = recordFixtureMonkey.giveMeBuilder(AdateUpdateDto.class)
									  .setNull("title")
									  .setNull("notes")
									  .setNull("location")
									  .setNull("alertWhen")
									  .setNull("repeatFreq")
									  .set("tagName", 구글_캘린더_태그.getName())
									  .setNotNull("ifAllDay")
									  .setNotNull("startsWhen")
									  .setNotNull("endsWhen")
									  .setNotNull("reminders")
									  .sample();
		제목_수정_요청 = recordFixtureMonkey.giveMeBuilder(AdateUpdateDto.class)
									  .setNotNull("title")
									  .setNull("notes")
									  .setNull("location")
									  .setNull("alertWhen")
									  .setNull("repeatFreq")
									  .setNull("tagName")
									  .setNotNull("ifAllDay")
									  .setNotNull("startsWhen")
									  .setNotNull("endsWhen")
									  .setNotNull("reminders")
									  .sample();
		위치_수정_요청 = recordFixtureMonkey.giveMeBuilder(AdateUpdateDto.class)
									  .setNull("title")
									  .setNull("notes")
									  .setNotNull("location")
									  .setNull("alertWhen")
									  .setNull("repeatFreq")
									  .setNull("tagName")
									  .setNotNull("ifAllDay")
									  .setNotNull("startsWhen")
									  .setNotNull("endsWhen")
									  .setNotNull("reminders")
									  .sample();
		노트_수정_요청 = recordFixtureMonkey.giveMeBuilder(AdateUpdateDto.class)
									  .setNull("title")
									  .setNotNull("notes")
									  .setNull("location")
									  .setNull("alertWhen")
									  .setNull("repeatFreq")
									  .setNull("tagName")
									  .setNotNull("ifAllDay")
									  .setNotNull("startsWhen")
									  .setNotNull("endsWhen")
									  .setNotNull("reminders")
									  .sample();
		알람_시간_수정_요청 = recordFixtureMonkey.giveMeBuilder(AdateUpdateDto.class)
										 .setNull("title")
										 .setNull("notes")
										 .setNull("location")
										 .setNotNull("alertWhen")
										 .setNull("repeatFreq")
										 .setNull("tagName")
										 .setNotNull("ifAllDay")
										 .setNotNull("startsWhen")
										 .setNotNull("endsWhen")
										 .setNotNull("reminders")
										 .sample();
		반복_시간_수정_요청 = recordFixtureMonkey.giveMeBuilder(AdateUpdateDto.class)
										 .setNull("title")
										 .setNull("notes")
										 .setNull("location")
										 .setNull("alertWhen")
										 .setNotNull("repeatFreq")
										 .setNull("tagName")
										 .setNotNull("ifAllDay")
										 .setNotNull("startsWhen")
										 .setNotNull("endsWhen")
										 .setNotNull("reminders")
										 .sample();
	}
}
