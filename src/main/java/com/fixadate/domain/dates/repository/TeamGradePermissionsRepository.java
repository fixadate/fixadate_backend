package com.fixadate.domain.dates.repository;

import com.fixadate.domain.auth.entity.BaseEntity.DataStatus;
import com.fixadate.domain.dates.entity.TeamGradePermissions;
import com.fixadate.domain.dates.entity.TeamMembers;
import com.fixadate.domain.dates.entity.Teams;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamGradePermissionsRepository extends JpaRepository<TeamGradePermissions, Long> {
}
