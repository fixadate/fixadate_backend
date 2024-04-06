package com.fixadate.global.util;

import com.google.api.client.util.DateTime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {
    static final String DATE_TIME_FORMATTER = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    static final String DATE_FORMATTER = "yyyy-MM-dd";

    public static LocalDateTime getLocalDateTimeFromDateTime(DateTime dateTime) {
        try {
            DateTimeFormatter f = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER);
            return LocalDateTime.parse(dateTime.toStringRfc3339(), f);
        } catch (Exception e) {
            //fixme 정보를 알려주는 log로 변경할 것
            e.printStackTrace();
            return null;
        }
    }
    public static LocalDateTime getLocalDateTimeFromDate(DateTime dateTime) {
        try {
            DateTimeFormatter f = DateTimeFormatter.ofPattern(DATE_FORMATTER);
            LocalDate localDateTime =  LocalDate.parse(dateTime.toStringRfc3339(), f);
            java.time.LocalTime localTime = LocalTime.MIDNIGHT;
            return LocalDateTime.of(localDateTime, localTime);
        } catch (Exception e) {
            //fixme 정보를 알려주는 log로 변경할 것
            e.printStackTrace();
            return null;
        }
    }
}
