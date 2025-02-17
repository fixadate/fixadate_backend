package com.fixadate.global.config.security;

import com.fixadate.domain.member.entity.Permissions;
import com.fixadate.domain.member.service.repository.PermissionsRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;

@Service
public class OldSecurityResourceService {

    private PermissionsRepository permissionsRepository;

    public OldSecurityResourceService(PermissionsRepository permissionsRepository) {
        this.permissionsRepository = permissionsRepository;
    }

    public LinkedHashMap<RequestMatcher, List<ConfigAttribute>> getResourceList() {

        LinkedHashMap<RequestMatcher, List<ConfigAttribute>> result = new LinkedHashMap<>();

        List<Permissions> resourcesList = new ArrayList<>();
//        Permissions permission1 = new Permissions(1L, "/aa", "GET");
//        Permissions permission2 = new Permissions(2L, "/bb", "GET");
//        Permissions permission3 = new Permissions(3L, "/cc", "GET");
//        Permissions permission4 = new Permissions(4L, "/dd", "GET");
//        Permissions permission5 = new Permissions(5L, "/ee", "GET");
//
//        Plans plan1 = new Plans(1L, PlanType.FREE, 0);
//        Plans plan2 = new Plans(2L, PlanType.BASIC, 10000);
//        Plans plan3 = new Plans(3L, PlanType.STANDARD, 20000);
//        Plans plan4 = new Plans(4L, PlanType.PREMIUM, 30000);
//        Plans plan5 = new Plans(5L, PlanType.ENTERPRISE, 40000);
//
//        PlanPermissions planPermission1 = new PlanPermissions(1L, plan1, permission1);
//        PlanPermissions planPermission2 = new PlanPermissions(2L, plan2, permission1);
//        PlanPermissions planPermission3 = new PlanPermissions(3L, plan2, permission2);
//        PlanPermissions planPermission4 = new PlanPermissions(4L, plan3, permission1);
//        PlanPermissions planPermission5 = new PlanPermissions(5L, plan3, permission2);
//        PlanPermissions planPermission6 = new PlanPermissions(6L, plan3, permission3);
//        PlanPermissions planPermission7 = new PlanPermissions(7L, plan4, permission1);
//        PlanPermissions planPermission8 = new PlanPermissions(8L, plan4, permission2);
//        PlanPermissions planPermission9 = new PlanPermissions(9L, plan4, permission3);
//        PlanPermissions planPermission10 = new PlanPermissions(10L, plan4, permission4);
//        PlanPermissions planPermission11 = new PlanPermissions(11L, plan5, permission1);
//        PlanPermissions planPermission12 = new PlanPermissions(12L, plan5, permission2);
//        PlanPermissions planPermission13 = new PlanPermissions(13L, plan5, permission3);
//        PlanPermissions planPermission14 = new PlanPermissions(14L, plan5, permission4);
//        PlanPermissions planPermission15 = new PlanPermissions(15L, plan5, permission5);
//
//        Set<PlanPermissions> planPermissions1 = new HashSet<>();
//        planPermissions1.add(planPermission1);
//        permission1.setPlanPermissions(planPermissions1);
//
//        Set<PlanPermissions> planPermissions2 = new HashSet<>();
//        planPermissions2.add(planPermission2);
//        planPermissions2.add(planPermission3);
//        permission2.setPlanPermissions(planPermissions2);
//
//        Set<PlanPermissions> planPermissions3 = new HashSet<>();
//        planPermissions3.add(planPermission4);
//        planPermissions3.add(planPermission5);
//        planPermissions3.add(planPermission6);
//        permission3.setPlanPermissions(planPermissions3);
//
//        Set<PlanPermissions> planPermissions4 = new HashSet<>();
//        planPermissions4.add(planPermission7);
//        planPermissions4.add(planPermission8);
//        planPermissions4.add(planPermission9);
//        planPermissions4.add(planPermission10);
//        permission4.setPlanPermissions(planPermissions4);
//
//        Set<PlanPermissions> planPermissions5 = new HashSet<>();
//        planPermissions5.add(planPermission11);
//        planPermissions5.add(planPermission12);
//        planPermissions5.add(planPermission13);
//        planPermissions5.add(planPermission14);
//        planPermissions5.add(planPermission15);
//        permission5.setPlanPermissions(planPermissions5);
//
//        resourcesList.add(permission1);
//        resourcesList.add(permission2);
//        resourcesList.add(permission3);
//        resourcesList.add(permission4);
//        resourcesList.add(permission5);

        resourcesList.forEach(re -> {
            re.getPlanPermissions().forEach(ro -> {
                AntPathRequestMatcher requestMatcher = new AntPathRequestMatcher(re.getPermissionName(), re.getHttpMethod());
                // 이미 존재하는 RequestMatcher인지 확인하고 ConfigAttribute를 추가
                result.computeIfAbsent(requestMatcher, k -> new ArrayList<>())
                    .add(new SecurityConfig(ro.getPlan().getName().toString()));
            });
        });
        return result;
    }
}