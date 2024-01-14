package com.fixadate.domain.member.exception;

public class AdateColorTypeNameDuplicatedException extends RuntimeException {
    public AdateColorTypeNameDuplicatedException(final String message) {
        super(message);
    }

    public AdateColorTypeNameDuplicatedException() {
        this("The name already exists.");
    }
}
