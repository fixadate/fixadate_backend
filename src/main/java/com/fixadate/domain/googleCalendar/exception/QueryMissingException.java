package com.fixadate.domain.googleCalendar.exception;

public class QueryMissingException extends RuntimeException {
    public QueryMissingException(final String message) {
        super(message);
    }

    public QueryMissingException() {
        this("No query sent. A query string is required.");
    }}
