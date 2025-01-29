package com.fixadate.domain.member.repository;

import com.fixadate.domain.member.entity.Permissions;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PermissionsJpaRepository extends JpaRepository<Permissions, Long> {
    Permissions findByPermissionNameAndHttpMethod(String permissionName, String httpMethod);

    @Query("SELECT p FROM Permissions p " +
        "LEFT JOIN p.planPermissions pp " +
        "LEFT JOIN pp.plan pl")
    List<Permissions> findAllResources();
}
