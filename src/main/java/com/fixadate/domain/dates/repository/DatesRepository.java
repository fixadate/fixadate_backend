package com.fixadate.domain.dates.repository;

import com.fixadate.domain.auth.entity.BaseEntity.DataStatus;
import com.fixadate.domain.dates.entity.Dates;
import com.fixadate.domain.dates.entity.Teams;
import com.fixadate.domain.member.entity.Member;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DatesRepository extends JpaRepository<Dates, Long> {
    List<Dates> findAllByTeamAndStatusIs(Teams team, DataStatus status);
    Optional<Dates> findByIdAndStatusIs(Long id,DataStatus status);

    List<Dates> findAllByTeam_IdAndStatusIs(Long teamId, DataStatus dataStatus);

    List<Dates> findByProponentAndStartsWhenBetween(Member proponent, LocalDateTime firstDayDateTime, LocalDateTime lastDayDateTime);
}
