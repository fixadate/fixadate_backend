package com.fixadate.domain.adate.repository;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.member.entity.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.fixadate.domain.adate.entity.QAdate.adate;

@Repository
@RequiredArgsConstructor
public class AdateQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public List<Adate> findByDateRange(Member member, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return jpaQueryFactory
                .selectFrom(adate)
                .where(adate.member.eq(member))
                .where(adate.endsWhen.goe(startDateTime)
                        .and(adate.startsWhen.loe(endDateTime)))
                .orderBy(adate.startsWhen.asc())
                .fetch();
    }
}
