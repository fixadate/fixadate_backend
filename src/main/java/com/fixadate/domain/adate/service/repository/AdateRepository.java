package com.fixadate.domain.adate.service.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.member.entity.Member;

public interface AdateRepository {

	Adate save(final Adate adate);

	void delete(final Adate adate);

	Optional<Adate> findAdateByCalendarId(final String calendarId);

	List<Adate> findByDateRange(
		final Member member,
		final LocalDateTime startDateTime,
		final LocalDateTime endDateTime
	);

    List<Adate> findByMemberAndBetweenDates(Member member, LocalDateTime firstDayDateTime, LocalDateTime lastDayDateTime);

	List<Adate> findOverlappingAdates(LocalDateTime targetStart, LocalDateTime targetEnd);
}
