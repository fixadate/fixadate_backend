package com.fixadate.global.jwt.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum JwtException {
    JWT_EXCEPTION(HttpStatus.BAD_REQUEST, "There is an issue with the token-related logic."),
    JWT_EXPIRED_EXCEPTION(HttpStatus.UNAUTHORIZED, "The token has expired."),
    JWT_UNSUPPORTED_EXCEPTION(HttpStatus.BAD_REQUEST,"Unsupported token format."),
    JWT_INVALID_EXCEPTION(HttpStatus.UNAUTHORIZED, "There is no valid member identifier in the JWT token."),
    JWT_BLACKLIST_EXCEPTION(HttpStatus.UNAUTHORIZED, "AccessToken is in BlackList");

    private final String description;
    private final HttpStatus status;

    private JwtException(HttpStatus status, String description) {
        this.description = description;
        this.status = status;
    }
}
