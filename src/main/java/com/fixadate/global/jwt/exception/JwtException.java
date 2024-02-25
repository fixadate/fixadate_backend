package com.fixadate.global.jwt.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum JwtException {
    JWT_EXCEPTION(HttpStatus.BAD_REQUEST, "There is an issue with the token-related logic."),
    JWT_EXPIRED_EXCEPTION(HttpStatus.UNAUTHORIZED, "The token has expired."),
    JWT_UNSUPPORTED_EXCEPTION(HttpStatus.BAD_REQUEST,"Unsupported token format.");

    private final String description;
    private final HttpStatus status;

    private JwtException(HttpStatus status, String description) {
        this.description = description;
        this.status = status;
    }
}
