package com.fixadate.global.exception;

import static com.fixadate.global.exception.ExceptionCode.DEFAULT_BAD_REQUEST;
import static org.springframework.http.HttpStatus.*;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.slf4j.SLF4JLogBuilder;
import org.apache.logging.slf4j.SLF4JLogger;
import org.springframework.http.HttpHeaders;
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

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
@Slf4j
public class ControllerAdvice extends ResponseEntityExceptionHandler {

	@ExceptionHandler({
		NotFoundException.class
	})
	public ResponseEntity<ExceptionResponse> handleNotFound(final NotFoundException exception) {
		log.error("404 | " + exception);

		return ResponseEntity.status(OK)  //404 NOT FOUND
							 .body(new ExceptionResponse(exception.getCode(), exception.getMessage()));
	}

	@ExceptionHandler({
		BadRequestException.class
	})
	public ResponseEntity<ExceptionResponse> handleBadRequest(final BadRequestException exception) {
		log.error("400 | " + exception);

		return ResponseEntity.status(OK)  //400 BAD REQUEST
							 .body(new ExceptionResponse(exception.getCode(), exception.getMessage()));
	}

	@ExceptionHandler({
		UnAuthorizedException.class
	})
	public ResponseEntity<ExceptionResponse> handleUnAuthorizedRequest(final UnAuthorizedException exception) {
		log.error("401 | " + exception);

		return ResponseEntity.status(OK)  //401 UNAUTHORIZED
							 .body(new ExceptionResponse(exception.getCode(), exception.getMessage()));
	}

	@ExceptionHandler({
		ForbiddenException.class
	})
	public ResponseEntity<ExceptionResponse> handleForbiddenRequest(final ForbiddenException exception) {
		log.error("403 | " + exception);

		return ResponseEntity.status(OK)  //403 FORBIDDEN
							 .body(new ExceptionResponse(exception.getCode(), exception.getMessage()));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException exception) {
		log.error("400 | " + exception);
		final String message = exception.getConstraintViolations().iterator().next().getMessage();

		return ResponseEntity.status(OK)  //400 BAD REQUEST
							 .body(new ExceptionResponse(DEFAULT_BAD_REQUEST.getCode(), message));
	}

	// TODO: [질문] validate 등 예외가 발생헀을 때에 대해서인데 code를 자체적인 번호로 부여해줘야 할까요?
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
		final MethodArgumentNotValidException exception,
		final HttpHeaders headers,
		final HttpStatusCode status,
		final WebRequest request
	) {
		log.error(exception.getStatusCode() + " | " + exception);
		final String message = exception.getFieldErrors().get(0).getDefaultMessage();

		return ResponseEntity.status(OK)
							 .body(new ExceptionResponse(DEFAULT_BAD_REQUEST.getCode(), message));
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(
		final Exception exception,
		final Object body,
		final HttpHeaders headers,
		final HttpStatusCode statusCode,
		final WebRequest request
	) {
		log.error("Internal Exception" + statusCode.value() + " | " + exception);

		return ResponseEntity.status(OK)
							 .body(new ExceptionResponse(statusCode.value(), exception.getMessage()));
	}
}
