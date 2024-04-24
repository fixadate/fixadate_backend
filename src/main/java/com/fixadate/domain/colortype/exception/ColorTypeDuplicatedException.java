package com.fixadate.domain.colortype.exception;

public class ColorTypeDuplicatedException extends RuntimeException {
    public ColorTypeDuplicatedException(final String message) {
        super(message);
    }

    public ColorTypeDuplicatedException() {
        this("The name already exists.");
    }
}
