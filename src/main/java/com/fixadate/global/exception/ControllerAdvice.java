package com.fixadate.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fixadate.global.exception.badRequest.BadRequestException;
import com.fixadate.global.exception.notFound.NotFoundException;
import com.fixadate.global.exception.unAuthorized.UnAuthorizedException;

@RestControllerAdvice
public class ControllerAdvice {
	@ExceptionHandler({
		NotFoundException.class
	})
	public ResponseEntity<ExceptionResponse> handleNotFound(final NotFoundException e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(new ExceptionResponse(e.getCode(), e.getMessage()));
	}

	@ExceptionHandler({
		BadRequestException.class
	})
	public ResponseEntity<ExceptionResponse> handleBadRequest(final BadRequestException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(new ExceptionResponse(e.getCode(), e.getMessage()));
	}

	@ExceptionHandler({
		UnAuthorizedException.class
	})
	public ResponseEntity<ExceptionResponse> handleUnAuthorizedRequest(final UnAuthorizedException e) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
			.body(new ExceptionResponse(e.getCode(), e.getMessage()));
	}
}
