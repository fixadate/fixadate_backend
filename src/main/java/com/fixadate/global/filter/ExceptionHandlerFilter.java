package com.fixadate.global.filter;

import static com.fixadate.global.exception.ExceptionCode.*;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fixadate.global.exception.ExceptionCode;
import com.fixadate.global.exception.unauthorized.TokenException;
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
			setErrorResponse(response, EXPIRED_PERIOD_TOKEN);
		} catch (MalformedJwtException exception) {
			setErrorResponse(response, INVALID_TOKEN);
		} catch (TokenException e) {
			if (e.getMessage().equals(INVALID_TOKEN_BLACKLIST.getMessage())) {
				setErrorResponse(response, INVALID_TOKEN_BLACKLIST);
			} else if (e.getMessage().equals(NOT_FOUND_MEMBER_IDENTIFIER.getMessage())) {
				setErrorResponse(response, NOT_FOUND_MEMBER_IDENTIFIER);
			}
		} catch (SecurityException | UnsupportedJwtException | IllegalArgumentException e) {
			setErrorResponse(response, FAIL_TO_VALIDATE_TOKEN);
		}
	}

	public void setErrorResponse(HttpServletResponse response, ExceptionCode exceptionCode) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		response.setStatus(exceptionCode.getCode());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("utf-8");
		JwtExceptionResponse errorResponse = new JwtExceptionResponse(exceptionCode.getMessage());
		response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
	}
}
