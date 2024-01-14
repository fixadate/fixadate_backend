package com.fixadate.global.jwt.exception;

public class TokenExpiredException extends RuntimeException{
    public TokenExpiredException(final String message) {
        super(message);
    }

    public TokenExpiredException() {
        this("The token has expired.");
    }
}
