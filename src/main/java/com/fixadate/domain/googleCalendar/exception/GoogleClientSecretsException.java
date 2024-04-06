package com.fixadate.domain.googleCalendar.exception;

public class GoogleClientSecretsException extends RuntimeException {
    public GoogleClientSecretsException(final String message) {
        super(message);
    }

    public GoogleClientSecretsException() {
        this("Error loading client secrets");
    }
}
