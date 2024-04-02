package com.fixadate.domain.googleCalendar.exception;

public class GoogleCalendarWatchException extends RuntimeException {
    public GoogleCalendarWatchException(final String message) {
        super(message);
    }

    public GoogleCalendarWatchException() {
        this("Error occurred while watching Google Calendar.");
    }
}
