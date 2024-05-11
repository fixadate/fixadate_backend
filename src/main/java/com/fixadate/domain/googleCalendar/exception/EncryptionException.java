package com.fixadate.domain.googleCalendar.exception;

public class EncryptionException extends RuntimeException {
    public EncryptionException(final String message) {
        super(message);
    }

    public EncryptionException(Exception e) {
        this("An exception occurred during encryption or decryption." +
                "errorMessage : " + e.getMessage());
    }

}
