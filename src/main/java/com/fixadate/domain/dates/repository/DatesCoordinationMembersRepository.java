package com.fixadate.domain.dates.repository;

import com.fixadate.domain.auth.entity.BaseEntity.DataStatus;
import com.fixadate.domain.dates.entity.DatesCoordinationMembers;
import com.fixadate.domain.dates.entity.DatesCoordinations;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DatesCoordinationMembersRepository extends JpaRepository<DatesCoordinationMembers, Long> {
    int countAllByDatesCoordinationsAndStatusIs(DatesCoordinations datesCoordinations, DataStatus status);

    List<DatesCoordinationMembers> findAllByDatesCoordinationsAndStatusIs(DatesCoordinations datesCoordinations, DataStatus dataStatus);
}
