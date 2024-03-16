package com.fixadate.domain.adate.exception;

public class GoogleCalendarWatchException extends RuntimeException {
    public GoogleCalendarWatchException(final String message) {
        super(message);
    }

    public GoogleCalendarWatchException() {
        this("Error occurred while watching Google Calendar.");
    }
}
