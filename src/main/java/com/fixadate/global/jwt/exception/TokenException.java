package com.fixadate.global.jwt.exception;

public class TokenException extends RuntimeException {
    public TokenException(final String message) {
        super(message);
    }

    public TokenException() {
        this("Token 관련 로직에서 문제가 발생하였습니다.");
    }
}
