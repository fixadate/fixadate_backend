package com.fixadate.global.jwt.exception;

public class RefreshTokenInvalidException extends RuntimeException {
    public RefreshTokenInvalidException(final String message) {
        super(message);
    }
    public RefreshTokenInvalidException() {
        this("The refreshToken does not exist or is invalid in Redis.");
    }

}
