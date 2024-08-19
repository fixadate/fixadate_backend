package com.fixadate.domain.adate.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.service.repository.AdateRepository;
import com.fixadate.domain.member.entity.Member;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AdateRepositoryImpl implements AdateRepository {

	private final AdateJpaRepository adateJpaRepository;
	private final AdateQueryRepository adateQueryRepository;

	@Override
	public Adate save(final Adate adate) {
		return adateJpaRepository.save(adate);
	}

	@Override
	public void delete(final Adate adate) {
		adateJpaRepository.delete(adate);
	}

	@Override
	public Optional<Adate> findAdateByCalendarId(final String calendarId) {
		return adateQueryRepository.findAdateByCalendarId(calendarId);
	}

	@Override
	public List<Adate> findByDateRange(
		final Member member,
		final LocalDateTime startDateTime,
		final LocalDateTime endDateTime
	) {
		return adateQueryRepository.findByDateRange(member, startDateTime, endDateTime);
	}
}