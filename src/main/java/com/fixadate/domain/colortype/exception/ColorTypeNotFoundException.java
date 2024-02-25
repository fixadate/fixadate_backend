package com.fixadate.domain.colortype.exception;

public class ColorTypeNotFoundException extends RuntimeException {
    public ColorTypeNotFoundException(final String message) {
        super(message);
    }

    public ColorTypeNotFoundException() {
        this("Cannot found ColorType in database");
    }
}
