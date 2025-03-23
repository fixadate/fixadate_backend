package com.fixadate.domain.adate.dto.response;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.main.dto.AdateInfo;
import com.fixadate.domain.main.dto.TagInfo;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record AdateResponse(
	String title,
	String notes,
	String location,
	LocalDateTime alertWhen,
	LocalDateTime repeatFreq,
	String tagName,
	String tagColor,
	boolean ifAllDay,
	String startDate,
	String startsWhen,
	String endsWhen,
	String calendarId,
	boolean reminders
) {
	public static AdateResponse of(Adate adate) {
		return new AdateResponse(
			adate.getTitle(),
			adate.getNotes(),
			adate.getLocation(),
			adate.getAlertWhen(),
			adate.getRepeatFreq(),
			adate.getTagName(),
			adate.getColor(),
			adate.isIfAllDay(),
			adate.getStartsWhen().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
			adate.getStartsWhen().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")),
			adate.getEndsWhen().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")),
			adate.getCalendarId(),
			adate.isReminders()
		);
	}
}
