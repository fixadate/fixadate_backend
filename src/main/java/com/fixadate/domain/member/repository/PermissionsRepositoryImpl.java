package com.fixadate.domain.member.repository;

import com.fixadate.domain.member.entity.Permissions;
import com.fixadate.domain.member.entity.PlanPermissions;
import com.fixadate.domain.member.entity.Plans;
import com.fixadate.domain.member.entity.Plans.PlanType;
import com.fixadate.domain.member.service.repository.PermissionsRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PermissionsRepositoryImpl implements PermissionsRepository {
	private final PermissionsJpaRepository permissionsJpaRepository;
	private final PlansJpaRepository plansJpaRepository;
	private final PlanPermissionsJpaRepository planPermissionsJpaRepository;

	@Override
	public List<Permissions> findByPlan(PlanType plan) {
		Optional<Plans> optionalPlans = plansJpaRepository.findByName(plan);
		if(optionalPlans.isEmpty()){
			return new ArrayList<>();
		}
		List<PlanPermissions> planPermissions = planPermissionsJpaRepository.findAllByPlan(optionalPlans.get());
		return planPermissions.stream().map(PlanPermissions::getPermission).toList();
	}
}
