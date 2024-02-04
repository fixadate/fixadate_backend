package com.fixadate.global.error;

import com.fixadate.domain.adate.exception.GoogleAccessTokenExpiredException;
import com.fixadate.domain.invitation.exception.InvitationNotFountException;
import com.fixadate.domain.member.exception.AdateColorTypeNameDuplicatedException;
import com.fixadate.domain.member.exception.UnknownMemberException;
import com.fixadate.global.auth.exception.MemberSigninException;
import com.fixadate.global.auth.exception.UnknownOAuthPlatformException;
import com.fixadate.global.jwt.exception.TokenException;
import com.fixadate.global.jwt.exception.TokenExpiredException;
import com.fixadate.global.jwt.exception.TokenUnsupportedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler({
            UnknownMemberException.class,
            UnknownOAuthPlatformException.class,
            SQLIntegrityConstraintViolationException.class,
            InvitationNotFountException.class,
            MemberSigninException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFound(final RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    @ExceptionHandler({
            TokenUnsupportedException.class,
            TokenException.class,
            AdateColorTypeNameDuplicatedException.class,
            GoogleAccessTokenExpiredException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(final RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }
    @ExceptionHandler({
            TokenExpiredException.class
    })
    public ResponseEntity<ErrorResponse> handleUnAuthorizedRequest(final RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(errorResponse);
    }
}
