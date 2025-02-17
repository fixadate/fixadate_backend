package com.fixadate.domain.member.repository;

import com.fixadate.domain.member.entity.PlanResources;
import com.fixadate.domain.member.entity.Plans;
import com.fixadate.domain.member.entity.ResourceType;
import com.fixadate.domain.member.entity.Resources;
import com.fixadate.domain.member.service.repository.PlanResourcesRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PlanResourcesRepositoryImpl implements PlanResourcesRepository {
    private final PlanResourcesJpaRepository planResourcesJpaRepository;
    @Override
    public ResourceType getResourceType(Plans plan, String resourceName) {
        return switch (resourceName) {
            case "ADATE" -> ResourceType.ADATE;
            case "TEAM" -> ResourceType.TEAM;
            case "DATES" -> ResourceType.DATES;
            default -> null;
        };
    }

    @Override
    public int getResourceMaxCnt(ResourceType resourceType, Plans plan) {
        List<PlanResources> planResources = planResourcesJpaRepository.findAllByPlan(plan);
        if(planResources.isEmpty()) {
            throw new RuntimeException("PlanResources not found");
        }
        List<Resources> resources = planResources.stream().map(PlanResources::getResource).toList();
        Optional<Resources> resource = resources.stream().filter(r -> r.getResourceName().equals(resourceType.name())).findFirst();
        if(resource.isEmpty()) {
            throw new RuntimeException("Resource not found");
        }
        return resource.get().getMaxCnt();
    }
}
