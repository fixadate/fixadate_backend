package com.fixadate.global.dto;


import com.fixadate.global.exception.ExceptionCode;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record GeneralResponseDto(
    //response Code
    String rspCd,

    //response Message
    String rspMsg,

    //response Datetime
    String rspDtti,

    //response body
    Object data
) {
    public static GeneralResponseDto create(String rspCd, String rspMsg, @Nullable Object data) {
        return new GeneralResponseDto(
            rspCd,
            rspMsg,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            data
        );
    }

    public static GeneralResponseDto fail(ExceptionCode exceptionCode) {
        return new GeneralResponseDto(
            String.valueOf(exceptionCode.getCode()),
            exceptionCode.getMessage(),
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            null
        );
    }

    public static GeneralResponseDto fail(String rspCd, String rspMsg, @Nullable Object data) {
        return new GeneralResponseDto(
                rspCd,
                rspMsg,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                data
        );
    }

    public static GeneralResponseDto success(String rspMsg, @Nullable Object data) {
        return new GeneralResponseDto(
            "200",
            rspMsg,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            data
        );
    }

    public static GeneralResponseDto fail(String rspMsg, @Nullable Object data) {
        return new GeneralResponseDto(
            "500",
            rspMsg,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            data
        );
    }

}
