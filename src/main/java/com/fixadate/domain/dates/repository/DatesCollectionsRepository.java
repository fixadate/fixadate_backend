package com.fixadate.domain.dates.repository;

import com.fixadate.domain.dates.entity.DatesCollections;
import com.fixadate.domain.dates.entity.TeamGradePermissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DatesCollectionsRepository extends JpaRepository<DatesCollections, Long> {
}
