package com.fixadate.global.util;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.fixadate.global.exception.ExceptionCode;
import com.fixadate.global.exception.badrequest.InvalidTimeException;
import com.google.api.client.util.DateTime;

public class TimeUtil {
	static final String DATE_TIME_FORMATTER = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
	static final String DATE_FORMATTER = "yyyy-MM-dd";

	private TimeUtil() {
	}

	public static LocalDateTime getLocalDateTimeFromDateTime(DateTime dateTime) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER);
		return LocalDateTime.parse(dateTime.toStringRfc3339(), formatter);
	}

	public static LocalDateTime getLocalDateTimeFromDate(DateTime dateTime) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
		LocalDate localDateTime = LocalDate.parse(dateTime.toStringRfc3339(), formatter);
		java.time.LocalTime localTime = LocalTime.MIDNIGHT;
		return LocalDateTime.of(localDateTime, localTime);
	}

	public static LocalDateTime getLocalDateTimeFromLocalDate(LocalDate localDate, boolean isFirstDay) {
		try {
			if (isFirstDay) {
				return localDate.atTime(0, 0, 0, 0);
			}
			return localDate.atTime(23, 59, 59, 59);
		} catch (DateTimeException e) {
			throw new InvalidTimeException(ExceptionCode.INVALID_LOCALDATE);
		}
	}

	public static LocalDateTime getLocalDateTimeFromYearAndMonth(int year, int month, boolean isFirstDay) {
		if (isFirstDay) {
			return LocalDateTime.of(year, month, 1, 0, 0, 0, 0);
		}
		return LocalDateTime.of(year, month, getLastDayOfMonth(month), 23, 59, 59, 59);
	}

	private static int getLastDayOfMonth(int month) {
		return switch (month) {
			case 1, 3, 5, 7, 8, 10, 12 -> 31;
			case 4, 6, 9, 11 -> 30;
			case 2 -> 29;
			default -> throw new InvalidTimeException(ExceptionCode.INVALID_MONTH);
		};
	}
}
