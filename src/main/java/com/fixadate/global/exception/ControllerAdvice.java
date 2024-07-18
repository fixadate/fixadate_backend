package com.fixadate.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fixadate.global.exception.badrequest.BadRequestException;
import com.fixadate.global.exception.notfound.NotFoundException;
import com.fixadate.global.exception.unauthorized.UnAuthorizedException;

@RestControllerAdvice
public class ControllerAdvice {
	@ExceptionHandler({
		NotFoundException.class
	})
	public ResponseEntity<ExceptionResponse> handleNotFound(final NotFoundException exception) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(new ExceptionResponse(exception.getCode(), exception.getMessage()));
	}

	@ExceptionHandler({
		BadRequestException.class
	})
	public ResponseEntity<ExceptionResponse> handleBadRequest(final BadRequestException exception) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(new ExceptionResponse(exception.getCode(), exception.getMessage()));
	}

	@ExceptionHandler({
		UnAuthorizedException.class
	})
	public ResponseEntity<ExceptionResponse> handleUnAuthorizedRequest(final UnAuthorizedException exception) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
			.body(new ExceptionResponse(exception.getCode(), exception.getMessage()));
	}
}
