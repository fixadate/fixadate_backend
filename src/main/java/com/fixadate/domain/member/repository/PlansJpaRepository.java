package com.fixadate.domain.member.repository;

import com.fixadate.domain.member.entity.Plans;
import com.fixadate.domain.member.entity.Plans.PlanType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlansJpaRepository extends JpaRepository<Plans, Long> {
    Optional<Plans> findByName(PlanType name);
}
