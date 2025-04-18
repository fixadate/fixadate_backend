package com.fixadate.global.filter;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.entity.MemberPlans;
import com.fixadate.domain.member.entity.Plans;
import com.fixadate.domain.member.repository.PlanPermissionsJpaRepository;
import com.fixadate.domain.member.service.repository.PlansPermissionsRepository;
import com.fixadate.global.jwt.MemberPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class SubscriptionAuthorizationFilter extends OncePerRequestFilter {

    private final PlansPermissionsRepository plansPermissionsRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 현재 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증되지 않은 사용자라면 다음 필터로 전달
        if (authentication == null || !authentication.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }

        MemberPrincipal memberPrincipal = (MemberPrincipal) authentication.getPrincipal();
        Member member = memberPrincipal.getMember();
        Plans planType = member.getMemberPlan().getPlan();

        // 특정 조건에 따라 접근 제한
        if (!Plans.PlanType.PREMIUM.equals(planType.getName()) && request.getRequestURI().startsWith("/premium")) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"error\": \"Access denied for "+ planType.getName() +" plan users.\"}");
            return;
        }

        // 조건을 통과하면 다음 필터로 전달
        filterChain.doFilter(request, response);
    }
}
