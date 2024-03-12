package com.fixadate.domain.adate.exception;

public class GoogleSecurityException extends RuntimeException {
    public GoogleSecurityException(final String message) {
        super(message);
    }

    public GoogleSecurityException() {
        this("A security issue occurred with Google services.");
    }
}
