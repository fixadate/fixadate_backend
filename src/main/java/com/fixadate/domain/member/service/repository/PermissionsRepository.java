package com.fixadate.domain.member.service.repository;

import com.fixadate.domain.member.entity.Permissions;
import com.fixadate.domain.member.entity.Plans.PlanType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PermissionsRepository{

    List<Permissions> findByPlan(PlanType plan);
}