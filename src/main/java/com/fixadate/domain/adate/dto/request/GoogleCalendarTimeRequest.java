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
    static final String TIME_MAX_VALUE = "-23-59-59";
    static final String TIME_MIN_VALUE = "-00-00-00";
    static final String DATE_TIME_FORMATTER = "yyyy-MM-dd-HH-mm-ss";
    static final String ASIA_ZONE_ID = "Asia/Seoul";

    public List<DateTime> getDateTimes() throws ParseException {
        List<DateTime> dateTimes = new ArrayList<>();
        dateTimes.add(getDateTimeMFromTimeMax(timeMax));
        dateTimes.add(getDateTimeMFromTimeMin(timeMin));
        return dateTimes;
    }

    private DateTime getDateTimeMFromTimeMax(String timeMax) throws ParseException {
        String time = timeMax.concat(TIME_MAX_VALUE);
        SimpleDateFormat format = new SimpleDateFormat(DATE_TIME_FORMATTER);
        Date date = format.parse(time);
        return new DateTime(date, TimeZone.getTimeZone(ASIA_ZONE_ID));
    }

    private DateTime getDateTimeMFromTimeMin(String timeMin) throws ParseException {
        String time = timeMin.concat(TIME_MIN_VALUE);
        SimpleDateFormat format = new SimpleDateFormat(DATE_TIME_FORMATTER);
        Date date = format.parse(time);
        return new DateTime(date, TimeZone.getTimeZone(ASIA_ZONE_ID));
    }
}
