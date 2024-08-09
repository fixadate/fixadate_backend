package com.fixadate.domain.adate.service.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.member.entity.Member;

public interface AdateRepository {

	Adate save(Adate adate);

	void delete(Adate adate);

	Optional<Adate> findAdateByCalendarId(String calendarId);

	List<Adate> findByDateRange(Member member, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
