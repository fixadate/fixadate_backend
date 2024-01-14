package com.fixadate.global.jwt.exception;

public class TokenUnsupportedException extends RuntimeException {
    public TokenUnsupportedException(final String message) {
        super(message);
    }
    public TokenUnsupportedException() {
        this("Unsupported token format.");
    }
}
