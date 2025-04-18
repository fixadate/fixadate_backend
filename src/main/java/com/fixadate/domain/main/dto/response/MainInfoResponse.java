package com.fixadate.domain.main.dto.response;

import com.fixadate.domain.adate.dto.response.AdateResponse;
import com.fixadate.domain.dates.dto.response.DatesResponse;
import com.fixadate.domain.main.dto.AdateInfo;
import com.fixadate.domain.main.dto.DatesInfo;
import com.fixadate.domain.main.dto.Schedule;
import com.fixadate.domain.main.dto.TodoInfo;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MainInfoResponse{

    List<DateInfo> dateList = new ArrayList<>();

    @Data
    public static class DateInfo{
        private String yyyyMm;
        private int weekNum;
        private String date;
        private String day;
        List<AdateResponse> adateInfoList;
        List<TodoInfo> todoInfoList;
        List<DatesResponse> datesInfoList;
        List<Schedule> scheduleList = new ArrayList<>();
    }

    public void setDateInfos(String yyyyMM, int weekNum, LocalDateTime firstDayDateTime, LocalDateTime lastDayDateTime){
        while (firstDayDateTime.isBefore(lastDayDateTime)) {
            DateInfo dateInfo = new DateInfo();
            dateInfo.setYyyyMm(firstDayDateTime.format(DateTimeFormatter.ofPattern("yyyyMM")));
            dateInfo.setWeekNum(weekNum);
            dateInfo.setDate(firstDayDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            dateInfo.setDay(firstDayDateTime.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()));

            this.dateList.add(dateInfo);

            firstDayDateTime = firstDayDateTime.plusDays(1);
        }
    }
}
