package com.fixadate.domain.adate.exception;

public class GoogleAccessTokenExpiredException extends RuntimeException {
    public GoogleAccessTokenExpiredException(final String message) {
        super(message);
    }

    public GoogleAccessTokenExpiredException() {
        this("Google Access Token has expired.");
    }
}
