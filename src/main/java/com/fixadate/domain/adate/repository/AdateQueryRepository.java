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

	// TODO: [질문] readonly 힌트를 설정한 이유가 뭘까요? @Tansactional(readonly=true)로는 적용이 안 되는 것일까요?
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
			.orderBy(adate.startsWhen.asc())
			.setHint("org.hibernate.readOnly", true)
			.fetch();
	}

	// TODO: [질문] 어떤 용도인가요? 삭제해도 될까요?
	public List<Adate> findByDateRangeTest(LocalDateTime startDateTime, LocalDateTime endDateTime) {
		return jpaQueryFactory
			.selectFrom(adate)
			.where(adate.startsWhen.loe(endDateTime)
								   .and(adate.endsWhen.goe(startDateTime)))
			.orderBy(adate.startsWhen.asc())
			.setHint("org.hibernate.readOnly", true)
			.fetch();
	}
}
