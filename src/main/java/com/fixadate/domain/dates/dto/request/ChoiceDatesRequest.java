package com.fixadate.domain.dates.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChoiceDatesRequest(
        @Schema(description = "시작일시", example = "yyyyMMddHHmm")
        String startsWhen,
        @Schema(description = "종료일시", example = "yyyyMMddHHmm")
        String endsWhen
    ){}