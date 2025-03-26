package com.fixadate.domain.dates.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DatesInfoResponse {

    List<DailyDatesInfo> dateList = new ArrayList<>();

    @Data
    public static class DailyDatesInfo {
        private String yyyyMm;
        private int weekNum;
        private String date;
        private String day;
        List<DatesResponse> datesResponseList = new ArrayList<>();
        private int totalADateAndTodoCnt;
        private boolean isToday = false;

        public void setTotalCnt(){
            this.totalADateAndTodoCnt = this.datesResponseList.size();
        }
    }

    public void setDateInfos(LocalDateTime firstDayDateTime, LocalDateTime lastDayDateTime){
        LocalDate today = LocalDate.now();
        while (firstDayDateTime.isBefore(lastDayDateTime)) {
            DailyDatesInfo dateInfo = new DailyDatesInfo();
            dateInfo.setYyyyMm(firstDayDateTime.format(DateTimeFormatter.ofPattern("yyyyMM")));

            LocalDate localDate = firstDayDateTime.toLocalDate();
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            int weekOfMonth = localDate.get(weekFields.weekOfMonth());

            dateInfo.setWeekNum(weekOfMonth);
            dateInfo.setDate(firstDayDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            dateInfo.setDay(firstDayDateTime.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()));

            // yyyyMMdd 형식의 문자열을 LocalDate로 변환
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate inputDate = LocalDate.parse(dateInfo.date, formatter);

            if(inputDate.equals(today)){
                dateInfo.setToday(true);
            }

            this.dateList.add(dateInfo);

            firstDayDateTime = firstDayDateTime.plusDays(1);
        }
    }
}
