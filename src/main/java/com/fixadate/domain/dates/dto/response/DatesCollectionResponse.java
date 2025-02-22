package com.fixadate.domain.dates.dto.response;

import java.util.List;

public record DatesCollectionResponse(
        String datesTitle,
        int minutes,
        long totalTeamMemberCnt,
        boolean isUserParticipated,
        List<Time> times
){
    public record Time(
        String startsWhen,
        String endsWhen,
        long memberCnt
    ){
    }
}