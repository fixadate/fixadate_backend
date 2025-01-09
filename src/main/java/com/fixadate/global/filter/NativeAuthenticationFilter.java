package com.fixadate.global.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fixadate.global.dto.GeneralResponseDto;
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
//			response.sendError(HttpStatus.OK.value(), "");
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			GeneralResponseDto responseDto = new GeneralResponseDto(
					"0N0A0E0001",
					"Native Authentication Failed. value(s) might be missing",
					LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:SS")).toString(),
					null
			);

			ObjectMapper objectMapper = new ObjectMapper();
			String jsonResponse = objectMapper.writeValueAsString(responseDto);

			PrintWriter writer = response.getWriter();
			writer.write(jsonResponse);
			writer.flush();

			return;
		}

		filterChain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		System.out.println(request.getRequestURI());
		return request.getRequestURI().contains("/v1/member/nickname") || request.getRequestURI().contains("/error")
			|| request.getRequestURI().contains("/swagger-ui") || request.getRequestURI()
			.contains("/swagger-resources")
			|| request.getRequestURI().contains("/v3/api-docs") || request.getRequestURI()
			.contains("/swagger-ui/index.html")
			|| request.getRequestURI().contains("/favicon.ico") || request.getRequestURI()
			.contains("/v1/google/loadtest") || request.getRequestURI()
				.contains("/v1/appVersion");
	}
}
