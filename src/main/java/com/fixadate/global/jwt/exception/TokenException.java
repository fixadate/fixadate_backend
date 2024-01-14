package com.fixadate.global.jwt.exception;

public class TokenException extends RuntimeException {
    public TokenException(final String message) {
        super(message);
    }

    public TokenException() {
        this("There is an issue with the token-related logic.");
    }
}
