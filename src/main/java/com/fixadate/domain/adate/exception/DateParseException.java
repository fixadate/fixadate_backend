package com.fixadate.domain.adate.exception;

public class DateParseException extends RuntimeException {
    public DateParseException(final String message) {
        super(message);
    }

    public DateParseException() {
        this("Failed to parse date");
    }
}
