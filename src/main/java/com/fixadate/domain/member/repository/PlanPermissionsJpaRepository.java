package com.fixadate.domain.member.repository;

import com.fixadate.domain.member.entity.PlanPermissions;
import com.fixadate.domain.member.entity.Plans;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface PlanPermissionsJpaRepository extends JpaRepository<PlanPermissions, Long> {
    List<PlanPermissions> findAllByPlan(Plans plan);
}
