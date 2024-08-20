package com.fixadate.global.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fixadate.global.exception.badrequest.BadRequestException;
import com.fixadate.global.exception.forbidden.ForbiddenException;
import com.fixadate.global.exception.notfound.NotFoundException;
import com.fixadate.global.exception.unauthorized.UnAuthorizedException;

@RestControllerAdvice
public class ControllerAdvice extends ResponseEntityExceptionHandler {

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

	@ExceptionHandler({
		ForbiddenException.class
	})
	public ResponseEntity<ExceptionResponse> handleForbiddenRequest(final ForbiddenException exception) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
							 .body(new ExceptionResponse(exception.getCode(), exception.getMessage()));
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
		final MethodArgumentNotValidException exception,
		final HttpHeaders headers,
		final HttpStatusCode status,
		final WebRequest request
	) {
		final String message = exception.getFieldErrors().get(0).getDefaultMessage();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							 .body(new ExceptionResponse(HttpStatus.BAD_REQUEST.value(), message));
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(
		final Exception exception,
		final Object body,
		final HttpHeaders headers,
		final HttpStatusCode statusCode,
		final WebRequest request
	) {
		return ResponseEntity.status(statusCode)
							 .body(new ExceptionResponse(statusCode.value(), exception.getMessage()));
	}
}
