package com.fixadate.global.config.security;

import com.fixadate.domain.member.entity.Permissions;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PermissionsRepository extends JpaRepository<Permissions, Long> {

    Permissions findByPermissionNameAndHttpMethod(String permissionName, String httpMethod);

    @Query("select distinct p from Permissions p "
        + "left join fetch p.planPermissions pp "
        + "left join fetch pp.plan")
    List<Permissions> findAllResources();
}