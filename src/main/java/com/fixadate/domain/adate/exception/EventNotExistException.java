package com.fixadate.domain.adate.exception;

public class EventNotExistException extends RuntimeException {
    public EventNotExistException(final String message) {
        super(message);
    }

    public EventNotExistException() {
        this("db에 해당 event가 존재하지 않습니다.");
    }
}
