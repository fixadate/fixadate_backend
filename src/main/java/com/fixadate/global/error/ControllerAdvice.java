package com.fixadate.global.error;

import com.fixadate.domain.adate.exception.GoogleAccessTokenExpiredException;
import com.fixadate.domain.colortype.exception.ColorTypeNameDuplicatedException;
import com.fixadate.domain.colortype.exception.ColorTypeNotFoundException;
import com.fixadate.domain.invitation.exception.InvitationNotFountException;
import com.fixadate.domain.member.exception.MemberNotFoundException;
import com.fixadate.global.auth.exception.MemberSigninException;
import com.fixadate.global.auth.exception.UnknownOAuthPlatformException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler({
            MemberNotFoundException.class,
            UnknownOAuthPlatformException.class,
            InvitationNotFountException.class,
            ColorTypeNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFound(final RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    @ExceptionHandler({
            ColorTypeNameDuplicatedException.class,
            GoogleAccessTokenExpiredException.class,
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(final RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler({
            MemberSigninException.class
    })
    public ResponseEntity<ErrorResponse> handleUnAuthorizedRequest(final RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(errorResponse);
    }
}
