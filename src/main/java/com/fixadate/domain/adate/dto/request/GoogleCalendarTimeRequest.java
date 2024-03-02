package com.fixadate.domain.adate.dto.request;

import com.google.api.client.util.DateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public record GoogleCalendarTimeRequest(
        @NotBlank String timeMax,
        @NotBlank String timeMin,
        @NotNull int range) {

    public List<DateTime> getDateTimes() throws ParseException {
        List<DateTime> dateTimes = new ArrayList<>();
        dateTimes.add(getDateTimeMFromTimeMax(timeMax));
        dateTimes.add(getDateTimeMFromTimeMin(timeMin));
        return dateTimes;
    }

    private DateTime getDateTimeMFromTimeMax(String timeMax) throws ParseException {
        String time = timeMax.concat("-23-59-59");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date date = format.parse(time);
        return new DateTime(date, TimeZone.getTimeZone("Asia/Seoul"));
    }

    private DateTime getDateTimeMFromTimeMin(String timeMin) throws ParseException {
        String time = timeMin.concat("-00-00-00");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date date = format.parse(time);
        return new DateTime(date, TimeZone.getTimeZone("Asia/Seoul"));
    }
}
