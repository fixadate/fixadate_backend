package com.fixadate.domain.dates.repository;

import com.fixadate.domain.auth.entity.BaseEntity.DataStatus;
import com.fixadate.domain.dates.entity.Dates;
import com.fixadate.domain.dates.entity.TeamMembers;
import com.fixadate.domain.dates.entity.Teams;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DatesRepository extends JpaRepository<Dates, Long> {
    List<Dates> findAllByTeamAndStatusIs(Teams team, DataStatus status);
    Optional<Dates> findByIdAndStatusIs(Long id,DataStatus status);
}
