package com.fixadate.global.error;

import com.fixadate.domain.adate.exception.AdateIOException;
import com.fixadate.domain.adate.exception.DateParseException;
import com.fixadate.domain.colortype.exception.ColorTypeDuplicatedException;
import com.fixadate.domain.colortype.exception.ColorTypeNotFoundException;
import com.fixadate.domain.googleCalendar.exception.*;
import com.fixadate.domain.invitation.exception.InvitationNotFountException;
import com.fixadate.domain.member.exception.MemberNotFoundException;
import com.fixadate.global.auth.exception.MemberSigninException;
import com.fixadate.global.auth.exception.MemberSignupException;
import com.fixadate.global.auth.exception.UnknownOAuthPlatformException;
import com.fixadate.global.jwt.exception.AccessTokenBlackListException;
import com.fixadate.global.jwt.exception.RefreshTokenInvalidException;
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
            ColorTypeNotFoundException.class,
            GoogleCredentialsNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFound(final RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    @ExceptionHandler({
            ColorTypeDuplicatedException.class,
            AdateIOException.class,
            DateParseException.class,
            GoogleSecurityException.class,
            GoogleCalendarWatchException.class,
            MemberSignupException.class,
            QueryMissingException.class,
            EncryptionException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(final RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler({
            MemberSigninException.class,
            RefreshTokenInvalidException.class,
            AccessTokenBlackListException.class
    })
    public ResponseEntity<ErrorResponse> handleUnAuthorizedRequest(final RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(errorResponse);
    }
}
