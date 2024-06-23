package com.fixadate.global.filter;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class NativeAuthenticationFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String contentType = request.getHeader("content-type");
		String fixVersion = request.getHeader("Fix_Version");
		String telephoneCarrier = request.getHeader("tccmobdvcd");
		String celno = request.getHeader("celno");
		String cacheControl = request.getHeader("cache-control");

		if (contentType == null || fixVersion == null || telephoneCarrier == null || celno == null
			|| cacheControl == null) {
			response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "x");
			return;
		}

		filterChain.doFilter(request, response);
	}
}