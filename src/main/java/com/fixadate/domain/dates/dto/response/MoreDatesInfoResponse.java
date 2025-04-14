package com.fixadate.domain.dates.dto.response;

import com.fixadate.domain.dates.entity.Dates;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MoreDatesInfoResponse {

    List<MoreDailyDatesInfo> datesList = new ArrayList<>();

    @Data
    public static class MoreDailyDatesInfo {
        private String date;
        private String title;
        private String startsTime;
        private String endsTime;
        private int totalParticipantCnt;
        private String userName;
        private boolean isToday = false;
        private boolean isTomorrow = false;

        public MoreDailyDatesInfo of(Dates dates, int totalParticipantCnt, String userName) {
            LocalDate today = LocalDate.now();
            LocalDate tomorrow = today.plusDays(1);

            MoreDailyDatesInfo response = new MoreDailyDatesInfo();
            response.setDate(dates.getStartsWhen().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
            response.setTitle(dates.getTitle());
            response.setStartsTime(dates.getStartsWhen().format(DateTimeFormatter.ofPattern("HH:mm")));
            response.setEndsTime(dates.getEndsWhen().format(DateTimeFormatter.ofPattern("HH:mm")));
            response.setTotalParticipantCnt(totalParticipantCnt);
            response.setUserName(userName);

            if(this.date.equals(today.format(DateTimeFormatter.ofPattern("yyyyMMdd")))){
                this.isToday = true;
            } else if(this.date.equals(tomorrow.format(DateTimeFormatter.ofPattern("yyyyMMdd")))){
                this.isTomorrow = true;
            }

            return response;
        }
    }
}
