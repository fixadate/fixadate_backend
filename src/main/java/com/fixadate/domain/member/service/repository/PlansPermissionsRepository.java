package com.fixadate.domain.member.service.repository;

import com.fixadate.domain.member.entity.PlanPermissions;
import com.fixadate.domain.member.entity.Plans;
import com.fixadate.domain.member.entity.Plans.PlanType;

import java.util.List;
import java.util.Optional;

public interface PlansPermissionsRepository {

	List<PlanPermissions> findAllByPlan(Plans plan);

}
