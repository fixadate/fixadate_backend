package com.fixadate.domain.invitation.exception;

public class InvitationNotFountException extends RuntimeException {
	public InvitationNotFountException(final String message) {
		super(message);
	}

	public InvitationNotFountException() {
		this("The invitation does not exist in the redis database");
	}
}
