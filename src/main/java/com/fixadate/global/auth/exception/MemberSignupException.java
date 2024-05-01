package com.fixadate.global.auth.exception;

public class MemberSignupException extends RuntimeException {
    public MemberSignupException(final String message) {
        super(message);
    }
    public MemberSignupException() {
        this("member exists in database");
    }

}
