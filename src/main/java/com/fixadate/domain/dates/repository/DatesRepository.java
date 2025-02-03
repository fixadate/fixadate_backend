package com.fixadate.domain.dates.repository;

import com.fixadate.domain.dates.entity.Dates;
import com.fixadate.domain.dates.entity.TeamMembers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DatesRepository extends JpaRepository<Dates, Long> {

}
