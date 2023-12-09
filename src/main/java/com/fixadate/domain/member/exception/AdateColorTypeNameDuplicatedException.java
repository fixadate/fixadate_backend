package com.fixadate.domain.member.exception;

public class AdateColorTypeNameDuplicatedException extends RuntimeException {
    public AdateColorTypeNameDuplicatedException(final String message) {
        super(message);
    }

    public AdateColorTypeNameDuplicatedException() {
        this("이미 해당 name이 존재합니다.");
    }
}
