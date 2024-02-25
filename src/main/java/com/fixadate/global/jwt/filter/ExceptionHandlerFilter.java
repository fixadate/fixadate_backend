package com.fixadate.global.jwt.filter;

import com.fixadate.global.error.ErrorResponse;
import com.fixadate.global.jwt.exception.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class ExceptionHandlerFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (TokenExpiredException exception) {
            setErrorResponse(response, JwtException.JWT_EXPIRED_EXCEPTION);
        } catch (TokenUnsupportedException exception) {
            setErrorResponse(response, JwtException.JWT_UNSUPPORTED_EXCEPTION);
        } catch (TokenException exception) {
            setErrorResponse(response, JwtException.JWT_EXCEPTION);
        }
    }
    public void setErrorResponse(HttpServletResponse response, JwtException jwtException) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(jwtException.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        JwtExceptionResponse errorResponse = new JwtExceptionResponse(jwtException.getStatus().value(), jwtException.getDescription());
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
