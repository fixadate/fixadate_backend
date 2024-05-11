package com.fixadate.domain.adate.exception;

public class InvalidDateTimeException extends RuntimeException {
	public InvalidDateTimeException(final String message) {
		super(message);
	}

	public InvalidDateTimeException(Exception e) {
		this(e.getMessage());
	}
}
