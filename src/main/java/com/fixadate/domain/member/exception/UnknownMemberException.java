package com.fixadate.domain.member.exception;

public class UnknownMemberException extends RuntimeException {
    public UnknownMemberException(final String message) {
        super(message);
    }

    public UnknownMemberException() {
        this("The member does not exist in the database.");
    }
}
