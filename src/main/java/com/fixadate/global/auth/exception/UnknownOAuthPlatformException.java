package com.fixadate.global.auth.exception;

public class UnknownOAuthPlatformException extends RuntimeException {
    public UnknownOAuthPlatformException(final String message) {
        super(message);
    }

    public UnknownOAuthPlatformException() {
        this("The OAuth platform does not exist.");
    }
}
