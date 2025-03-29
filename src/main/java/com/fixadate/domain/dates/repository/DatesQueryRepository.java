package com.fixadate.domain.dates.repository;

import static com.fixadate.domain.dates.entity.QDates.dates;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.auth.entity.BaseEntity.DataStatus;
import com.fixadate.domain.dates.entity.Dates;
import com.fixadate.domain.member.entity.Member;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DatesQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public List<Dates> findByDateRange(
		final Member member,
		final LocalDateTime startDateTime,
		final LocalDateTime endDateTime
	) {
		return jpaQueryFactory
			.selectFrom(dates)
			.where(
				dates.member.eq(member)
							.and(dates.startsWhen.loe(endDateTime))
							.and(dates.endsWhen.goe(startDateTime))
							.and(dates.status.eq(DataStatus.ACTIVE))
			)
//			.leftJoin(dates.tag).fetchJoin()
			.orderBy(dates.startsWhen.asc())
			.fetch();
	}

	public List<Dates> findOverlappingDates(LocalDateTime targetStart, LocalDateTime targetEnd) {
		BooleanExpression condition = dates.startsWhen.between(targetStart, targetEnd)
			.or(dates.endsWhen.between(targetStart, targetEnd))
			.or(dates.startsWhen.loe(targetStart).and(dates.endsWhen.goe(targetEnd)))
			.and(dates.status.eq(DataStatus.ACTIVE));

		return jpaQueryFactory
			.selectFrom(dates)
			.where(condition)
			.fetch();
	}
}
