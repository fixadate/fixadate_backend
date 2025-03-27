package com.fixadate.domain.dates.repository;

import com.fixadate.domain.dates.entity.DatesCoordinationMembers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DatesCoordinationMembersRepository extends JpaRepository<DatesCoordinationMembers, Long> {
}
