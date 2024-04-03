package com.fixadate.domain.googleCalendar.exception;

public class GoogleNextSyncTokenException extends RuntimeException {
    public GoogleNextSyncTokenException(final String message) {
        super(message);
    }

    public GoogleNextSyncTokenException() {
        this("An error occurred while saving the nextSyncToken.");
    }
}
