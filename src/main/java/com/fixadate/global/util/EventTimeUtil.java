package com.fixadate.global.util;

import java.time.LocalDateTime;

import com.google.api.services.calendar.model.EventDateTime;

public class EventTimeUtil extends TimeUtil {

	public static LocalDateTime checkEventDateTimeIsNull(final EventDateTime eventDateTime) {
		if (eventDateTime.getDateTime() == null) {
			return getLocalDateTimeFromDate(eventDateTime.getDate());
		}

		return getLocalDateTimeFromDateTime(eventDateTime.getDateTime());
	}

	public static boolean checkEventIsAllDayType(final EventDateTime eventDateTime) {
		return eventDateTime.getDateTime() == null;
	}
}
