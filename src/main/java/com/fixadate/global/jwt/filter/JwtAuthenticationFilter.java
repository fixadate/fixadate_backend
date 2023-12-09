package com.fixadate.global.jwt.filter;

import com.fixadate.global.jwt.service.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String jwt = retrieveToken(request);
        if (StringUtils.hasText(jwt) && jwtProvider.validateToken(jwt)) {
            SecurityContextHolder
                    .getContext()
                    .setAuthentication(
                            jwtProvider.getAuthentication(jwt)
                    );
        } else {
            log.info("토큰이 없습니다. 익명 사용자로 처리합니다.");
        }
        filterChain.doFilter(request, response);
    }


    private String retrieveToken(HttpServletRequest httpServletRequest) {
        String bearerToken = httpServletRequest.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
