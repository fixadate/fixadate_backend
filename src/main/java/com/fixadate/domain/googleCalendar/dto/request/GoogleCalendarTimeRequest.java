package com.fixadate.domain.googleCalendar.dto.request;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.fixadate.domain.adate.exception.DateParseException;
import com.google.api.client.util.DateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GoogleCalendarTimeRequest(
	@NotBlank String timeMax,
	@NotBlank String timeMin,
	@NotNull int range) {
	static final String TIME_MAX_VALUE = "-23-59-59";
	static final String TIME_MIN_VALUE = "-00-00-00";
	static final String DATE_TIME_FORMATTER = "yyyy-MM-dd-HH-mm-ss";
	static final String ASIA_ZONE_ID = "Asia/Seoul";

	public List<DateTime> getDateTimes() {
		List<DateTime> dateTimes = new ArrayList<>();
		dateTimes.add(getDateTimeMFromTime(TIME_MAX_VALUE));
		dateTimes.add(getDateTimeMFromTime(TIME_MIN_VALUE));
		return dateTimes;
	}

	private DateTime getDateTimeMFromTime(String t) {
		try {
			String time = timeMax.concat(t);
			SimpleDateFormat format = new SimpleDateFormat(DATE_TIME_FORMATTER);
			Date date = format.parse(time);
			return new DateTime(date, TimeZone.getTimeZone(ASIA_ZONE_ID));
		} catch (ParseException e) {
			throw new DateParseException();
		}
	}
}
