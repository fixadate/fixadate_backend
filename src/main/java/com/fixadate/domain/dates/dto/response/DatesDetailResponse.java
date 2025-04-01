package com.fixadate.domain.dates.dto.response;

import com.fixadate.domain.dates.entity.Dates;
import com.fixadate.domain.dates.entity.DatesMembers;
import com.fixadate.domain.member.entity.Member;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record DatesDetailResponse(
        String title,
        Proponent proponent,
        List<DatesParticipants> DatesParticipants,
        String startsWhen,
        String endsWhen,
        boolean isProponent
    ){

    public static DatesDetailResponse of(Dates dates,List<DatesParticipants> DatesParticipants, boolean isProponent){
        Member proponent = dates.getProponent();
        return new DatesDetailResponse(
            dates.getTitle(),
            new Proponent(proponent.getName(), proponent.getProfileImg()),
            DatesParticipants,
            dates.getStartsWhen().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")),
            dates.getEndsWhen().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")),
            isProponent
        );

    }

    public record Proponent(
        String name,
        String profileUrl
    ){}

    public record DatesParticipants(
        String name,
        String profileUrl
    ){
        public static DatesParticipants of(DatesMembers datesMembers){
            Member member = datesMembers.getMember();
            return new DatesParticipants(member.getName(), member.getProfileImg());
        }
    }
}