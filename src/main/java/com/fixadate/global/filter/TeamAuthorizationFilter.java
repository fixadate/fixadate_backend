package com.fixadate.global.filter;

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
public class TeamAuthorizationFilter extends OncePerRequestFilter {

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

//        // 사용자 권한 및 구독 플랜 확인 (예: CustomUserDetails 사용)
//        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
//        String subscriptionPlan = userDetails.getSubscriptionPlan(); // 사용자 구독 플랜 정보
//        String role = userDetails.getRole(); // 사용자 역할 정보
//
//        // 특정 조건에 따라 접근 제한
//        if ("BASIC".equals(subscriptionPlan) && request.getRequestURI().startsWith("/premium")) {
//            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//            response.setContentType("application/json");
//            response.setCharacterEncoding("UTF-8");
//            response.getWriter().write("{\"error\": \"Access denied for BASIC plan users.\"}");
//            return;
//        }
//
//        if ("USER".equals(role) && request.getRequestURI().startsWith("/admin")) {
//            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//            response.setContentType("application/json");
//            response.setCharacterEncoding("UTF-8");
//            response.getWriter().write("{\"error\": \"Admin access only.\"}");
//            return;
//        }

        // 조건을 통과하면 다음 필터로 전달
        filterChain.doFilter(request, response);
    }
}
