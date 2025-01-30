package com.fixadate.domain.member.repository;

import com.fixadate.domain.member.entity.Plans;
import com.fixadate.domain.member.entity.Plans.PlanType;
import com.fixadate.domain.member.service.repository.PlansRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PlansRepositoryImpl implements PlansRepository {

	private final PlansJpaRepository plansJpaRepository;
	@Override
	public Optional<Plans> findPlansByName(PlanType name) {
		return plansJpaRepository.findByName(name);
	}
}
