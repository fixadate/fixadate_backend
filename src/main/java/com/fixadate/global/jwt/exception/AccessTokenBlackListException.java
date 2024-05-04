package com.fixadate.global.jwt.exception;

public class AccessTokenBlackListException extends RuntimeException {
    public AccessTokenBlackListException(final String message) {
        super(message);
    }
    public AccessTokenBlackListException() {
        this("AccessToken is in BlackList");
    }
}
