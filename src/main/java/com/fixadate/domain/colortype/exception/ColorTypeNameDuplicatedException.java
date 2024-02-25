package com.fixadate.domain.colortype.exception;

public class ColorTypeNameDuplicatedException extends RuntimeException {
    public ColorTypeNameDuplicatedException(final String message) {
        super(message);
    }

    public ColorTypeNameDuplicatedException() {
        this("The name already exists.");
    }
}
