package com.fixadate.global.jwt.exception;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record JwtExceptionResponse(String message) {
}
