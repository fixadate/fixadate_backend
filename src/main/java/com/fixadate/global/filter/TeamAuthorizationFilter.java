package com.fixadate.global.filter;

import com.fixadate.domain.dates.entity.Grades;
import com.fixadate.domain.dates.entity.TeamMembers;
import com.fixadate.domain.dates.repository.TeamGradePermissionsRepository;
import com.fixadate.domain.dates.repository.TeamMembersRepository;
import com.fixadate.domain.dates.repository.TeamRepository;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.entity.Plans;
import com.fixadate.global.jwt.MemberPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerMapping;

@RequiredArgsConstructor
public class TeamAuthorizationFilter extends OncePerRequestFilter {
    private final TeamMembersRepository teamMembersRepository;
    private final TeamGradePermissionsRepository teamGradePermissionsRepository;
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

        // 현재 요청의 PathVariable 가져오기
        @SuppressWarnings("unchecked")
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        if (pathVariables != null && pathVariables.containsKey("teamId")) {
            Long teamId = Long.parseLong(pathVariables.get("teamId"));
            Optional<TeamMembers> teamMembersOptional = teamMembersRepository.findByTeam_IdAndMember_Id(teamId, member.getId());
            boolean isMember = teamMembersOptional.isPresent();

            // 팀에 속하지 않은 사용자라면 접근 제한
            if (!isMember) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"error\": \"Access denied for this team.\"}");
                return;
            }
            Grades grade = teamMembersOptional.get().getGrades();
            // 특정 조건에 따라 접근 제한
            if ((!Grades.OWNER.equals(grade) && !Grades.MANAGER.equals(grade)) && request.getRequestURI().startsWith("/api/team/grade")) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"error\": \"Access denied for "+ grade.name() +" grade team member.\"}");
                return;
            }

        }

        // 조건을 통과하면 다음 필터로 전달
        filterChain.doFilter(request, response);
    }
}
