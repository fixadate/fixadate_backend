package com.fixadate.domain.adate.repository;

import static com.fixadate.domain.adate.entity.QAdate.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.member.entity.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AdateQueryRepository {
	private final JPAQueryFactory jpaQueryFactory;

	public List<Adate> findByDateRange(Member member, LocalDateTime startDateTime, LocalDateTime endDateTime) {
		return jpaQueryFactory
			.selectFrom(adate)
			.where(adate.member.eq(member)
				.and(adate.startsWhen.loe(endDateTime))
				.and(adate.endsWhen.goe(startDateTime)))
			.orderBy(adate.startsWhen.asc())
			.setHint("org.hibernate.readOnly", true)
			.fetch();
	}

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
