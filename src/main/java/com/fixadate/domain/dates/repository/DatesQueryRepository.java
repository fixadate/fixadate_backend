package com.fixadate.domain.dates.repository;

import static com.fixadate.domain.dates.entity.QDates.dates;
import static com.fixadate.domain.dates.entity.QDatesMembers.datesMembers;

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
				dates.proponent.eq(member)
							.and(dates.startsWhen.loe(endDateTime))
							.and(dates.endsWhen.goe(startDateTime))
							.and(dates.status.eq(DataStatus.ACTIVE))
			)
//			.leftJoin(dates.tag).fetchJoin()
			.orderBy(dates.startsWhen.asc())
			.fetch();
	}

	public List<Dates> findOverlappingDates(Member member, LocalDateTime targetStart, LocalDateTime targetEnd) {
		BooleanExpression overlapsCondition =
			dates.startsWhen.between(targetStart, targetEnd)
				.or(dates.endsWhen.between(targetStart, targetEnd))
				.or(dates.startsWhen.loe(targetStart).and(dates.endsWhen.goe(targetEnd)));

		return jpaQueryFactory
			.selectFrom(dates)
			.join(datesMembers).on(dates.eq(datesMembers.dates)) // 직접 조인
			.where(datesMembers.member.eq(member)) // 특정 사용자가 참여한 일정만 필터링
			.where(overlapsCondition) // 일정이 겹치는 경우 필터링
			.where(dates.status.eq(DataStatus.ACTIVE)) // 활성화된 일정만 조회
			.fetch();
	}
}
