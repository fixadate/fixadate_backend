package com.fixadate.domain.member.repository;

import com.fixadate.domain.member.entity.PlanResources;
import com.fixadate.domain.member.entity.Plans;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.parameters.P;

public interface PlanResourcesJpaRepository extends JpaRepository<PlanResources, Long> {
    List<PlanResources> findAllByPlan(Plans plan);
}
