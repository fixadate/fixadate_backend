package com.fixadate.domain.dates.dto.response;

import java.util.List;

public record DatesCollectionsResultResponse(
    String title,
    boolean isOwnerConfirmed,
    List<DatesCollectionResponse.Time> times
){
    public record Time(
        String startsWhen,
        String endsWhen,
        long memberCnt
    ){
    }
}