package com.fixadate.domain.member.exception;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(final String message) {
        super(message);
    }

    public MemberNotFoundException() {
        this("The member does not exist in the database.");
    }
}
