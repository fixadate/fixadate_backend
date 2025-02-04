package com.fixadate.domain.member.repository;

import com.fixadate.domain.member.entity.PlanPermissions;
import com.fixadate.domain.member.entity.Plans;
import com.fixadate.domain.member.entity.Plans.PlanType;
import com.fixadate.domain.member.service.repository.PlansPermissionsRepository;
import com.fixadate.domain.member.service.repository.PlansRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PlansPermissionsRepositoryImpl implements PlansPermissionsRepository {

	private final PlanPermissionsJpaRepository planPermissionsJpaRepository;
	@Override
	public List<PlanPermissions> findAllByPlan(Plans plan){
		return planPermissionsJpaRepository.findAllByPlan(plan);
	}
}
