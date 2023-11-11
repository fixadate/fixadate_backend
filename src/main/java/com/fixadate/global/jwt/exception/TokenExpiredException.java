package com.fixadate.global.jwt.exception;

public class TokenExpiredException extends RuntimeException{
    public TokenExpiredException(final String message) {
        super(message);
    }

    public TokenExpiredException() {
        this("Token이 만료되었습니다.");
    }
}
