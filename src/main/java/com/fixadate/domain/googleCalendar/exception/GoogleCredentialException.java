package com.fixadate.domain.googleCalendar.exception;

public class GoogleCredentialException extends RuntimeException{
    public GoogleCredentialException(final String message) {
        super(message);
    }

    public GoogleCredentialException() {
        this("Error getting credentials");
    }
}
