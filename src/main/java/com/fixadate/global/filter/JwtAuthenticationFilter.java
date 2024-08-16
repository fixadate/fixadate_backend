package com.fixadate.global.filter;

import java.io.IOException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fixadate.global.jwt.service.JwtProvider;

import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtProvider jwtProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException, MalformedJwtException {
		String jwt = jwtProvider.retrieveToken(request);
		if (StringUtils.hasText(jwt) && jwtProvider.validateToken(jwt) && !jwtProvider.isTokenBlackList(jwt)) {
			SecurityContextHolder
				.getContext()
				.setAuthentication(
					jwtProvider.getAuthentication(jwt)
				);
		}
		filterChain.doFilter(request, response);
	}

}
