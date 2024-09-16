package com.fixadate.domain.adate.dto.response;

import java.time.LocalDateTime;

import com.fixadate.domain.tag.dto.response.TagResponse;

public record AdateViewResponse(
	String title,
	String notes,
	boolean ifAllDay,
	LocalDateTime startsWhen,
	LocalDateTime endsWhen,
	String calendarId,
	TagResponse tagResponse
) {
}
