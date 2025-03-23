package com.fixadate.domain.adate.repository;

import com.fixadate.domain.member.entity.Member;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fixadate.domain.adate.entity.Adate;

@Repository
public interface AdateJpaRepository extends JpaRepository<Adate, Long> {

    List<Adate> findByMemberAndStartsWhenBetween(Member member, LocalDateTime firstDayDateTime, LocalDateTime lastDayDateTime);
}
