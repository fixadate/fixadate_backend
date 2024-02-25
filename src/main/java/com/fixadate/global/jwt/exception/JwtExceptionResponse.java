package com.fixadate.global.jwt.exception;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.map.annotate.JsonSerialize;

//fixme jsonAutoDetect, JsonSerialize에 대해 글 작성하기
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record JwtExceptionResponse(int status, String message) {
}