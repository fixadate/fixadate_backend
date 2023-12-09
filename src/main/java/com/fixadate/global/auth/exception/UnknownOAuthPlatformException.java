package com.fixadate.global.auth.exception;

public class UnknownOAuthPlatformException extends RuntimeException {
    public UnknownOAuthPlatformException(final String message) {
        super(message);
    }

    public UnknownOAuthPlatformException() {
        this("존재하지 않는 OAuthPlatform입니다.");
    }
}
