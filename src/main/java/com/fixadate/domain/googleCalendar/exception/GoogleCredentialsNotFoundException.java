package com.fixadate.domain.googleCalendar.exception;

public class GoogleCredentialsNotFoundException extends RuntimeException {
    public GoogleCredentialsNotFoundException(final String message) {
        super(message);
    }

    public GoogleCredentialsNotFoundException() {
        this("An error occurred while saving googleCredentials");
    }
}
