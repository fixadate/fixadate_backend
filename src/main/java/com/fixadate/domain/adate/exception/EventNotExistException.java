package com.fixadate.domain.adate.exception;

public class EventNotExistException extends RuntimeException {
	public EventNotExistException(final String message) {
		super(message);
	}

	public EventNotExistException() {
		this("The event does not exist in the database.");
	}
}
