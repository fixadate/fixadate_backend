package com.fixadate.domain.member.service.repository;

import com.fixadate.domain.member.entity.Plans;
import com.fixadate.domain.member.entity.Plans.PlanType;
import java.util.Optional;

public interface PlansRepository {

	Optional<Plans> findPlansByName(final PlanType name);

}
