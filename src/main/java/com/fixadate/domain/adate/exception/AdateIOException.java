package com.fixadate.domain.adate.exception;

import java.io.IOException;

public class AdateIOException extends RuntimeException {
	public AdateIOException(final String message) {
		super(message);
	}

	public AdateIOException(IOException e) {
		this(e.getMessage());
	}
}
