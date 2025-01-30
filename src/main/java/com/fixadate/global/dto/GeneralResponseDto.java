package com.fixadate.global.dto;


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

}
