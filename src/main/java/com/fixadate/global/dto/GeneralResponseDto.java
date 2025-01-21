package com.fixadate.global.dto;


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
}
