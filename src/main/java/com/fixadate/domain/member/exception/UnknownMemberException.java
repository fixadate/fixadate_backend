package com.fixadate.domain.member.exception;

public class UnknownMemberException extends RuntimeException {
    public UnknownMemberException(final String message) {
        super(message);
    }

    public UnknownMemberException() {
        this("member를 찾을 수 없습니다.");
    }
}
