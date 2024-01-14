package com.fixadate.global.auth.exception;

public class MemberSigninException extends RuntimeException {
    public MemberSigninException(final String message) {
        super(message);
    }
    public MemberSigninException() {
        this("The member does not exist in the database.");
    }
}
