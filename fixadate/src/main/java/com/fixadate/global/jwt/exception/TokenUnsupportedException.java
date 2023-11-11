package com.fixadate.global.jwt.exception;

public class TokenUnsupportedException extends RuntimeException {
    public TokenUnsupportedException(final String message) {
        super(message);
    }
    public TokenUnsupportedException() {
        this("지원하지 않는 형식의 token입니다.");
    }
}
