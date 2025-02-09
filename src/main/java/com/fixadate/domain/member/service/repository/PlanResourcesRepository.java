package com.fixadate.domain.member.service.repository;

import com.fixadate.domain.member.entity.Plans;
import com.fixadate.domain.member.entity.ResourceType;

public interface PlanResourcesRepository {
    public ResourceType getResourceType(Plans plan, String resourceName);
    public int getResourceMaxCnt(ResourceType resourceType, Plans plan);

}
