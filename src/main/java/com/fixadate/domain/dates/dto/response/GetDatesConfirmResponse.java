package com.fixadate.domain.dates.dto.response;

import java.util.List;

public record GetDatesConfirmResponse(
        int totalMemberCnt,
        int minutes,
        List<DatesCollectionsInfo> datesCollections
      ) {  public record DatesCollectionsInfo(
        String startsWhen,
        String endsWhen,
        int memberCnt
    ){}
}