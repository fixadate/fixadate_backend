package com.fixadate.global.config.security;

import com.fixadate.domain.member.entity.Permissions;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;

@Service
public class SecurityResourceService {

    private PermissionsRepository permissionsRepository;

    public SecurityResourceService(PermissionsRepository permissionsRepository) {
        this.permissionsRepository = permissionsRepository;
    }

    public LinkedHashMap<RequestMatcher, List<ConfigAttribute>> getResourceList() {

        LinkedHashMap<RequestMatcher, List<ConfigAttribute>> result = new LinkedHashMap<>();
        List<Permissions> resourcesList = permissionsRepository.findAllResources();

        resourcesList.forEach(re ->
                {
                    List<ConfigAttribute> configAttributeList = new ArrayList<>();
                    re.getPlanPermissions().forEach(ro -> {
                        configAttributeList.add(new SecurityConfig(ro.getPlan().getName().toString()));
                        result.put(new AntPathRequestMatcher(re.getPermissionName(), re.getHttpMethod()), configAttributeList);
                    });
                }
        );
        return result;
    }
}