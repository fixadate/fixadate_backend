package com.fixadate.domain.adate.dto.response;

import java.time.LocalDateTime;

// TODO: [질문] AdateDto까지는 Boolean으로 null이 가능하기에 npe 가능성이 있습니다. 괜찮을까요?
public record AdateResponse(
	String title,
	String notes,
	String location,
	LocalDateTime alertWhen,
	LocalDateTime repeatFreq,
	String color,
	boolean ifAllDay,
	LocalDateTime startsWhen,
	LocalDateTime endsWhen,
	String calendarId,
	boolean reminders
) {
}
