package com.fixadate.domain.dates.dto.response;

import java.util.List;

public record DatesDetailResponse(
        String title,
        Owner owner,
        List<DatesParticipants> DatesParticipants,
        String startsWhen,
        String endsWhen,
        boolean isOwner
    ){
    public record Owner(
        String name,
        String profileUrl
    ){}

    public record DatesParticipants(
        String name,
        String profileUrl
    ){}
}