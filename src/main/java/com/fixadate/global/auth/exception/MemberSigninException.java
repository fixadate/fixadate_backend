package com.fixadate.global.auth.exception;

public class MemberSigninException extends RuntimeException {
    public MemberSigninException(final String message) {
        super(message);
    }
    public MemberSigninException() {
        this("db에 멤버가 존재하지 않습니다.");
    }
}
