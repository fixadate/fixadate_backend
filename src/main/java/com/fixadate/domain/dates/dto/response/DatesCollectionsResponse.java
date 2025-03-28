package com.fixadate.domain.dates.dto.response;

import java.time.DayOfWeek;
import java.util.List;

public record DatesCollectionsResponse(
        String title,
        String time,
        int totalMemberCnt,
        List<DatesCollectionDateInfo> dateInfos
    ){
        public record DatesCollectionDateInfo(
            String day,
            String date,
            List <DatesCollectionMinuteInfo> minuteInfos
        ){
            public DatesCollectionDateInfo of(DayOfWeek dayOfWeek, String date, List <DatesCollectionMinuteInfo> minuteInfos){
                return new DatesCollectionDateInfo(dayOfWeek.name().substring(0,3), date, minuteInfos);
            }
        }

        public record DatesCollectionMinuteInfo(
            String time,
            int memberCnt
        ){
            public DatesCollectionMinuteInfo of(String time, int memberCnt){
                return new DatesCollectionMinuteInfo(time, memberCnt);
            }
        }
    }