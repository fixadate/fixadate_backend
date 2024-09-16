package com.fixadate.domain.adate.repository;

import static com.fixadate.domain.adate.entity.QAdate.adate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.member.entity.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AdateQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public Optional<Adate> findAdateByCalendarId(final String calendarId) {
		return Optional.ofNullable(
			jpaQueryFactory.selectFrom(adate)
						   .where(adate.calendarId.eq(calendarId))
						   .leftJoin(adate.member).fetchJoin()
						   .leftJoin(adate.tag).fetchJoin()
						   .fetchOne()
		);
	}

	public List<Adate> findByDateRange(
		final Member member,
		final LocalDateTime startDateTime,
		final LocalDateTime endDateTime
	) {
		return jpaQueryFactory
			.selectFrom(adate)
			.where(
				adate.member.eq(member)
							.and(adate.startsWhen.loe(endDateTime))
							.and(adate.endsWhen.goe(startDateTime))
			)
			.leftJoin(adate.tag).fetchJoin()
			.orderBy(adate.startsWhen.asc())
			.fetch();
	}
}
