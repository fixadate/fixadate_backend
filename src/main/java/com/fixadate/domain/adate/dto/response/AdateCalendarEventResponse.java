package com.fixadate.domain.adate.dto.response;

import java.time.LocalDateTime;

import com.fixadate.domain.adate.entity.Adate;

public record AdateCalendarEventResponse(
	String title,
	String notes,
	String location,
	LocalDateTime alertWhen,
	LocalDateTime repeatFreq,
	String color,
	String adateName,
	boolean ifAllDay,
	LocalDateTime startsWhen,
	LocalDateTime endsWhen,
	String calendarId,
	boolean reminders,
	String status
) {

	public static AdateCalendarEventResponse of(Adate adate) {
		return new AdateCalendarEventResponse(
			adate.getTitle(),
			adate.getNotes(),
			adate.getLocation(),
			adate.getAlertWhen(),
			adate.getRepeatFreq(),
			adate.getColor(),
			adate.getAdateName(),
			adate.getIfAllDay(),
			adate.getStartsWhen(),
			adate.getEndsWhen(),
			adate.getCalendarId(),
			adate.isReminders(),
			adate.getStatus()
		);
	}
}
