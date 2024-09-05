package com.fixadate.domain.adate.service;

import static com.fixadate.global.util.constant.ConstantValue.ADATE_WITH_COLON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fixadate.domain.adate.dto.AdateDto;
import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.service.fixture.AdateServiceFixture;
import com.fixadate.domain.adate.service.repository.AdateRepository;
import com.fixadate.domain.tag.event.object.TagSettingEvent;
import com.fixadate.global.exception.badrequest.InvalidTimeException;
import com.fixadate.global.exception.notfound.AdateNotFoundException;
import com.fixadate.global.exception.notfound.TagNotFoundException;
import com.fixadate.global.util.constant.ExternalCalendar;

@SpringBootTest
@Testcontainers
@RecordApplicationEvents
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class AdateServiceTest extends AdateServiceFixture {

	private static final int REPEATED_COUNT = 100;

	@Autowired
	private AdateService adateService;

	@Autowired
	private AdateRepository adateRepository;

	@Autowired
	private ApplicationEvents events;

	@Autowired
	private RedisTemplate<Object, Object> redisJsonTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	@Nested
	@DisplayName("adate 저장 테스트")
	class RegisterAdateEventTest {

		@RepeatedTest(REPEATED_COUNT)
		void adate를_저장한다() {
			// given
			final List<Adate> originAdate = adateRepository.findByDateRange(
				회원,
				일정_저장_요청.startsWhen(),
				일정_저장_요청.endsWhen()
			);

			// when
			adateService.registerAdate(일정_저장_요청, 회원);
			final List<Adate> actual = adateRepository.findByDateRange(회원, 일정_저장_요청.startsWhen(), 일정_저장_요청.endsWhen());

			// then
			assertThat(actual).hasSize(originAdate.size() + 1);
		}

		@RepeatedTest(REPEATED_COUNT)
		void 태그를_조회하는_이벤트를_한_번_호출한다() {
			// when
			adateService.registerAdate(태그가_있는_일정_저장_요청, 회원);
			final long actual = events.stream(TagSettingEvent.class).count();

			// then
			assertThat(actual).isEqualTo(1);
		}

		@RepeatedTest(REPEATED_COUNT)
		void 태그가_없다면_태그_저장_이벤트는_호출하지_않는다() {
			// when
			adateService.registerAdate(태그가_없는_일정_저장_요청, 회원);
			final long actual = events.stream(TagSettingEvent.class).count();

			// then
			assertThat(actual).isEqualTo(0);
		}

		@RepeatedTest(REPEATED_COUNT)
		void 존재하지_않는_태그로_예외가_발생해도_adate는_저장한다() {
			// given
			final List<Adate> originAdate = adateRepository.findByDateRange(
				회원,
				존재하지_않는_태그를_통해_일정_저장_요청.startsWhen(),
				존재하지_않는_태그를_통해_일정_저장_요청.endsWhen()
			);

			// when
			assertThatThrownBy(() -> adateService.registerAdate(존재하지_않는_태그를_통해_일정_저장_요청, 회원))
				.isInstanceOf(TagNotFoundException.class);
			final List<Adate> actual = adateRepository.findByDateRange(
				회원,
				존재하지_않는_태그를_통해_일정_저장_요청.startsWhen(),
				존재하지_않는_태그를_통해_일정_저장_요청.endsWhen()
			);

			// then
			assertThat(actual).hasSize(originAdate.size() + 1);
		}
	}

	@Nested
	@DisplayName("캘린더 아이디를 통해 삭제한 adate를 복구한다")
	class RestoreAdateByCalendarIdTest {

		@RepeatedTest(REPEATED_COUNT)
		void adate를_복구한다() {
			// when
			final AdateDto actual = adateService.restoreAdateByCalendarId(삭제된_일정.getCalendarId());

			// then
			assertSoftly(softly -> {
				softly.assertThat(actual.title()).isEqualTo(삭제된_일정.getTitle());
				softly.assertThat(actual.notes()).isEqualTo(삭제된_일정.getNotes());
				softly.assertThat(actual.location()).isEqualTo(삭제된_일정.getLocation());
				softly.assertThat(actual.alertWhen()).isEqualTo(삭제된_일정.getAlertWhen());
				softly.assertThat(actual.repeatFreq()).isEqualTo(삭제된_일정.getRepeatFreq());
				softly.assertThat(actual.color()).isEqualTo(삭제된_일정.getColor());
				softly.assertThat(actual.ifAllDay()).isEqualTo(삭제된_일정.isIfAllDay());
				softly.assertThat(actual.startsWhen()).isEqualTo(삭제된_일정.getStartsWhen());
				softly.assertThat(actual.endsWhen()).isEqualTo(삭제된_일정.getEndsWhen());
				softly.assertThat(actual.calendarId()).isEqualTo(삭제된_일정.getCalendarId());
				softly.assertThat(actual.reminders()).isEqualTo(삭제된_일정.isReminders());
			});
		}
	}

	@Nested
	@DisplayName("외부 일정 저장 시 tag 자동 세팅 테스트")
	class RegisterEventTest {

		@RepeatedTest(REPEATED_COUNT)
		void 구글_캘린더_일정을_저장한다() {
			// given
			final List<Adate> originAdate = adateRepository.findByDateRange(
				회원,
				외부_캘린더_일정.getStartsWhen(),
				외부_캘린더_일정.getEndsWhen()
			);

			// when
			adateService.registerExternalCalendarToAdate(외부_캘린더_일정, ExternalCalendar.GOOGLE);
			final List<Adate> actual = adateRepository.findByDateRange(
				회원,
				외부_캘린더_일정.getStartsWhen(),
				외부_캘린더_일정.getEndsWhen()
			);

			// then
			assertThat(actual).hasSize(originAdate.size() + 1);
		}

		@RepeatedTest(REPEATED_COUNT)
		void 저장시_태그를_저장하는_이벤트를_한_번_호출한다() {
			// when
			adateService.registerExternalCalendarToAdate(외부_캘린더_일정, ExternalCalendar.GOOGLE);
			final long actual = events.stream(TagSettingEvent.class).count();

			// then
			assertThat(actual).isEqualTo(1);
		}
	}

	@Nested
	@DisplayName("adate 객체를 통한 일정 삭제 테스트")
	class RemoveAdateTest {

		@RepeatedTest(REPEATED_COUNT)
		void 일정을_삭제한다() {
			// when
			adateService.removeAdate(저장된_일정);
			final Optional<Adate> actual = adateRepository.findAdateByCalendarId(저장된_일정.getCalendarId());

			// then
			assertThat(actual).isEmpty();
		}
	}

	@Nested
	@DisplayName("캘린더 아이디를 통한 일정 삭제 테스트")
	class RemoveAdateByCalendarIdTest {

		@RepeatedTest(REPEATED_COUNT)
		void 일정을_삭제한다() {
			// when
			adateService.removeAdateByCalendarId(저장된_일정.getCalendarId());
			final Optional<Adate> actual = adateRepository.findAdateByCalendarId(저장된_일정.getCalendarId());
			final Object redisResult = redisJsonTemplate.opsForValue()
														.get(ADATE_WITH_COLON.getValue() + 저장된_일정.getCalendarId());
			final Adate actualRedis = objectMapper.convertValue(redisResult, Adate.class);

			// then
			assertSoftly(softly -> {
				assertThat(actual).isEmpty();
				assertThat(actualRedis).isEqualTo(저장된_일정);
			});
		}

		@RepeatedTest(REPEATED_COUNT)
		void 존재하지_않는_캘린더_아이디라면_예외를_반환한다() {
			// when & then
			assertThatThrownBy(() -> adateService.removeAdateByCalendarId(존재하지_않는_일정의_캘린더_아이디))
				.isInstanceOf(AdateNotFoundException.class);
		}
	}

	@Nested
	@DisplayName("캘린더 아이디로 일정 조회 테스트 - adate 반환")
	class GetAdateByCalendarIdTest {

		@RepeatedTest(REPEATED_COUNT)
		void 일정을_조회() {
			// when
			final Optional<Adate> actual = adateService.getAdateByCalendarId(저장된_일정.getCalendarId());

			// then
			assertSoftly(softly -> {
				softly.assertThat(actual).isPresent();
				softly.assertThat(actual.get()).isEqualTo(저장된_일정);
			});
		}

		@RepeatedTest(REPEATED_COUNT)
		void 존재하지_않는_캘린더_아이디라면_예외를_반환한다() {
			// when
			final Optional<Adate> actual = adateService.getAdateByCalendarId(존재하지_않는_일정의_캘린더_아이디);

			// then
			assertThat(actual).isEmpty();
		}
	}

	@Nested
	@DisplayName("캘린더 아이디로 일정 조회 테스트 - dto 반환")
	class GetAdateInformationByCalendarIdTest {

		@RepeatedTest(REPEATED_COUNT)
		void 일정을_조회한다() {
			// when
			final AdateDto actual = adateService.getAdateInformationByCalendarId(저장된_일정.getCalendarId());

			// then
			assertThat(actual.id()).isEqualTo(저장된_일정.getId());
		}

		@RepeatedTest(REPEATED_COUNT)
		void 존재하지_않는_캘린더_아이디라면_예외를_반환한다() {
			// when && then
			assertThatThrownBy(() -> adateService.getAdateInformationByCalendarId(존재하지_않는_일정의_캘린더_아이디))
				.isInstanceOf(AdateNotFoundException.class);
		}
	}

	@Nested
	@DisplayName("회원, 시작 일시, 종료 일시를 통한 일정 조회 테스트")
	class GetAdateByStartAndEndTimeTest {

		@RepeatedTest(REPEATED_COUNT)
		void 일정을_조회한다() {
			// when
			final List<AdateDto> actual = adateService.getAdateByStartAndEndTime(회원, 시작_일시, 종료_일시);

			// then
			assertSoftly(softly -> {
				softly.assertThat(actual).hasSize(2);
				softly.assertThat(actual.get(0).id()).isEqualTo(일정1.getId());
				softly.assertThat(actual.get(1).id()).isEqualTo(일정2.getId());
			});
		}

		@RepeatedTest(REPEATED_COUNT)
		void 일정이_없다면_빈_배열을_반환한다() {
			// when
			final List<AdateDto> actual = adateService.getAdateByStartAndEndTime(회원, 일정_없는_시작_일시, 일정_없는_종료_일시);

			// then
			assertThat(actual).isEmpty();
		}
	}

	@Nested
	@DisplayName("회원, 연도, 달을 통한 일정 조회 테스트")
	class GetAdatesByMonthTest {

		@RepeatedTest(REPEATED_COUNT)
		void 일정을_조회한다() {
			// when
			final List<AdateDto> actual = adateService.getAdatesByMonth(
				회원,
				시작_일시.getYear(),
				시작_일시.getMonthValue()
			);

			// then
			assertSoftly(softly -> {
				softly.assertThat(actual).hasSize(1);
				softly.assertThat(actual.get(0).id()).isEqualTo(일정1.getId());
			});
		}

		@RepeatedTest(REPEATED_COUNT)
		void 일정이_없다면_빈_배열을_반환한다() {
			// when
			final List<AdateDto> actual = adateService.getAdatesByMonth(
				회원,
				일정_없는_시작_일시.getYear(),
				일정_없는_시작_일시.getMonthValue()
			);

			// then
			assertThat(actual).isEmpty();
		}
	}

	@Nested
	@DisplayName("회원, 시작일, 종료일을 통한 일정 조회 테스트")
	class GetAdatesByWeekTest {

		@RepeatedTest(REPEATED_COUNT)
		void 일정을_조회한다() {
			// when
			final List<AdateDto> actual = adateService.getAdatesByDate(
				회원,
				시작_일시.toLocalDate(),
				종료_일시.toLocalDate()
			);

			// then
			assertSoftly(softly -> {
				softly.assertThat(actual).hasSize(2);
				softly.assertThat(actual.get(0).id()).isEqualTo(일정1.getId());
				softly.assertThat(actual.get(1).id()).isEqualTo(일정2.getId());
			});
		}

		@RepeatedTest(REPEATED_COUNT)
		void 시작일보다_종료일이_빠르다면_예외를_반환한다() {
			// when & then
			assertThatThrownBy(() -> adateService.getAdatesByDate(회원, 종료_일시.toLocalDate(), 시작_일시.toLocalDate()))
				.isInstanceOf(InvalidTimeException.class);
		}
	}

	@Nested
	@DisplayName("일정 수정 테스트 (종일여부, 시작, 종료, 리마이드 여부 정보 필수)")
	class UpdateAdateTest {

		@RepeatedTest(REPEATED_COUNT)
		void 일정의_모든_정보를_수정한다() {
			// when
			final AdateDto actual = adateService.updateAdate(회원, 태그가_있는_일정.getCalendarId(), 전체_수정_요청);

			// then
			assertSoftly(softly -> {
				softly.assertThat(actual.id()).isEqualTo(태그가_있는_일정.getId());
				softly.assertThat(actual.title()).isEqualTo(전체_수정_요청.title());
				softly.assertThat(actual.notes()).isEqualTo(전체_수정_요청.notes());
				softly.assertThat(actual.location()).isEqualTo(전체_수정_요청.location());
				softly.assertThat(actual.alertWhen()).isEqualTo(전체_수정_요청.alertWhen());
				softly.assertThat(actual.repeatFreq()).isEqualTo(전체_수정_요청.repeatFreq());
				softly.assertThat(actual.tag().name()).isEqualTo(전체_수정_요청.tagName());
				softly.assertThat(actual.ifAllDay()).isEqualTo(전체_수정_요청.ifAllDay());
				softly.assertThat(actual.startsWhen()).isEqualTo(전체_수정_요청.startsWhen());
				softly.assertThat(actual.endsWhen()).isEqualTo(전체_수정_요청.endsWhen());
				softly.assertThat(actual.reminders()).isEqualTo(전체_수정_요청.reminders());
			});
		}

		@RepeatedTest(REPEATED_COUNT)
		void 일정의_제목을_수정한다() {
			// when
			final AdateDto actual = adateService.updateAdate(회원, 태그가_있는_일정.getCalendarId(), 제목_수정_요청);

			// then
			assertSoftly(softly -> {
				softly.assertThat(actual.id()).isEqualTo(태그가_있는_일정.getId());
				softly.assertThat(actual.title()).isEqualTo(제목_수정_요청.title());
				softly.assertThat(actual.notes()).isEqualTo(태그가_있는_일정.getNotes());
				softly.assertThat(actual.location()).isEqualTo(태그가_있는_일정.getLocation());
				softly.assertThat(actual.alertWhen()).isEqualTo(태그가_있는_일정.getAlertWhen());
				softly.assertThat(actual.repeatFreq()).isEqualTo(태그가_있는_일정.getRepeatFreq());
				softly.assertThat(actual.tag().name()).isEqualTo(태그가_있는_일정.getTag().getName());
				softly.assertThat(actual.ifAllDay()).isEqualTo(제목_수정_요청.ifAllDay());
				softly.assertThat(actual.startsWhen()).isEqualTo(제목_수정_요청.startsWhen());
				softly.assertThat(actual.endsWhen()).isEqualTo(제목_수정_요청.endsWhen());
				softly.assertThat(actual.reminders()).isEqualTo(제목_수정_요청.reminders());
			});
		}

		@RepeatedTest(REPEATED_COUNT)
		void 일정의_노트를_수정한다() {
			// when
			final AdateDto actual = adateService.updateAdate(회원, 태그가_있는_일정.getCalendarId(), 노트_수정_요청);

			// then
			assertSoftly(softly -> {
				softly.assertThat(actual.id()).isEqualTo(태그가_있는_일정.getId());
				softly.assertThat(actual.title()).isEqualTo(태그가_있는_일정.getTitle());
				softly.assertThat(actual.notes()).isEqualTo(노트_수정_요청.notes());
				softly.assertThat(actual.location()).isEqualTo(태그가_있는_일정.getLocation());
				softly.assertThat(actual.alertWhen()).isEqualTo(태그가_있는_일정.getAlertWhen());
				softly.assertThat(actual.repeatFreq()).isEqualTo(태그가_있는_일정.getRepeatFreq());
				softly.assertThat(actual.tag().name()).isEqualTo(태그가_있는_일정.getTag().getName());
				softly.assertThat(actual.ifAllDay()).isEqualTo(노트_수정_요청.ifAllDay());
				softly.assertThat(actual.startsWhen()).isEqualTo(노트_수정_요청.startsWhen());
				softly.assertThat(actual.endsWhen()).isEqualTo(노트_수정_요청.endsWhen());
				softly.assertThat(actual.reminders()).isEqualTo(노트_수정_요청.reminders());
			});
		}

		@RepeatedTest(REPEATED_COUNT)
		void 일정의_위치를_수정한다() {
			// when
			final AdateDto actual = adateService.updateAdate(회원, 태그가_있는_일정.getCalendarId(), 위치_수정_요청);

			// then
			assertSoftly(softly -> {
				softly.assertThat(actual.id()).isEqualTo(태그가_있는_일정.getId());
				softly.assertThat(actual.title()).isEqualTo(태그가_있는_일정.getTitle());
				softly.assertThat(actual.notes()).isEqualTo(태그가_있는_일정.getNotes());
				softly.assertThat(actual.location()).isEqualTo(위치_수정_요청.location());
				softly.assertThat(actual.alertWhen()).isEqualTo(태그가_있는_일정.getAlertWhen());
				softly.assertThat(actual.repeatFreq()).isEqualTo(태그가_있는_일정.getRepeatFreq());
				softly.assertThat(actual.tag().name()).isEqualTo(태그가_있는_일정.getTag().getName());
				softly.assertThat(actual.ifAllDay()).isEqualTo(위치_수정_요청.ifAllDay());
				softly.assertThat(actual.startsWhen()).isEqualTo(위치_수정_요청.startsWhen());
				softly.assertThat(actual.endsWhen()).isEqualTo(위치_수정_요청.endsWhen());
				softly.assertThat(actual.reminders()).isEqualTo(위치_수정_요청.reminders());
			});
		}

		@RepeatedTest(REPEATED_COUNT)
		void 일정의_알람_시간을_수정한다() {
			// when
			final AdateDto actual = adateService.updateAdate(회원, 태그가_있는_일정.getCalendarId(), 알람_시간_수정_요청);

			// then
			assertSoftly(softly -> {
				softly.assertThat(actual.id()).isEqualTo(태그가_있는_일정.getId());
				softly.assertThat(actual.title()).isEqualTo(태그가_있는_일정.getTitle());
				softly.assertThat(actual.notes()).isEqualTo(태그가_있는_일정.getNotes());
				softly.assertThat(actual.location()).isEqualTo(태그가_있는_일정.getLocation());
				softly.assertThat(actual.alertWhen()).isEqualTo(알람_시간_수정_요청.alertWhen());
				softly.assertThat(actual.repeatFreq()).isEqualTo(태그가_있는_일정.getRepeatFreq());
				softly.assertThat(actual.tag().name()).isEqualTo(태그가_있는_일정.getTag().getName());
				softly.assertThat(actual.ifAllDay()).isEqualTo(알람_시간_수정_요청.ifAllDay());
				softly.assertThat(actual.startsWhen()).isEqualTo(알람_시간_수정_요청.startsWhen());
				softly.assertThat(actual.endsWhen()).isEqualTo(알람_시간_수정_요청.endsWhen());
				softly.assertThat(actual.reminders()).isEqualTo(알람_시간_수정_요청.reminders());
			});
		}

		@RepeatedTest(REPEATED_COUNT)
		void 일정의_반복_시간을_수정한다() {
			// when
			final AdateDto actual = adateService.updateAdate(회원, 태그가_있는_일정.getCalendarId(), 반복_시간_수정_요청);

			// then
			assertSoftly(softly -> {
				softly.assertThat(actual.id()).isEqualTo(태그가_있는_일정.getId());
				softly.assertThat(actual.title()).isEqualTo(태그가_있는_일정.getTitle());
				softly.assertThat(actual.notes()).isEqualTo(태그가_있는_일정.getNotes());
				softly.assertThat(actual.location()).isEqualTo(태그가_있는_일정.getLocation());
				softly.assertThat(actual.alertWhen()).isEqualTo(태그가_있는_일정.getAlertWhen());
				softly.assertThat(actual.repeatFreq()).isEqualTo(반복_시간_수정_요청.repeatFreq());
				softly.assertThat(actual.tag().name()).isEqualTo(태그가_있는_일정.getTag().getName());
				softly.assertThat(actual.ifAllDay()).isEqualTo(반복_시간_수정_요청.ifAllDay());
				softly.assertThat(actual.startsWhen()).isEqualTo(반복_시간_수정_요청.startsWhen());
				softly.assertThat(actual.endsWhen()).isEqualTo(반복_시간_수정_요청.endsWhen());
				softly.assertThat(actual.reminders()).isEqualTo(반복_시간_수정_요청.reminders());
			});
		}

		@RepeatedTest(REPEATED_COUNT)
		void 일정의_태그를_수정한다() {
			// when
			final AdateDto actual = adateService.updateAdate(회원, 태그가_있는_일정.getCalendarId(), 태그_수정_요청);

			// then
			assertSoftly(softly -> {
				softly.assertThat(actual.id()).isEqualTo(태그가_있는_일정.getId());
				softly.assertThat(actual.title()).isEqualTo(태그가_있는_일정.getTitle());
				softly.assertThat(actual.notes()).isEqualTo(태그가_있는_일정.getNotes());
				softly.assertThat(actual.location()).isEqualTo(태그가_있는_일정.getLocation());
				softly.assertThat(actual.alertWhen()).isEqualTo(태그가_있는_일정.getAlertWhen());
				softly.assertThat(actual.repeatFreq()).isEqualTo(태그가_있는_일정.getRepeatFreq());
				softly.assertThat(actual.tag().name()).isEqualTo(태그_수정_요청.tagName());
				softly.assertThat(actual.ifAllDay()).isEqualTo(태그_수정_요청.ifAllDay());
				softly.assertThat(actual.startsWhen()).isEqualTo(태그_수정_요청.startsWhen());
				softly.assertThat(actual.endsWhen()).isEqualTo(태그_수정_요청.endsWhen());
				softly.assertThat(actual.reminders()).isEqualTo(태그_수정_요청.reminders());
			});
		}
	}
}
