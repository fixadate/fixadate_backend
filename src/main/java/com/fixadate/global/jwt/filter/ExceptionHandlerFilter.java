package com.fixadate.global.jwt.filter;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fixadate.global.jwt.exception.AccessTokenBlackListException;
import com.fixadate.global.jwt.exception.JwtException;
import com.fixadate.global.jwt.exception.JwtExceptionResponse;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ExceptionHandlerFilter extends OncePerRequestFilter {
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		try {
			filterChain.doFilter(request, response);
		} catch (ExpiredJwtException exception) {
			setErrorResponse(response, JwtException.JWT_EXPIRED_EXCEPTION);
		} catch (MalformedJwtException exception) {
			setErrorResponse(response, JwtException.JWT_INVALID_EXCEPTION);
		} catch (AccessTokenBlackListException e) {
			setErrorResponse(response, JwtException.JWT_BLACKLIST_EXCEPTION);
		} catch (SecurityException | UnsupportedJwtException | IllegalArgumentException e) {
			setErrorResponse(response, JwtException.JWT_EXCEPTION);
		}
	}

	public void setErrorResponse(HttpServletResponse response, JwtException jwtException) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		response.setStatus(jwtException.getStatus().value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		JwtExceptionResponse errorResponse = new JwtExceptionResponse(jwtException.getStatus().value(),
			jwtException.getDescription());
		response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
	}
}
