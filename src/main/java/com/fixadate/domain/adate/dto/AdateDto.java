package com.fixadate.domain.adate.dto;

import java.time.LocalDateTime;

import com.fixadate.domain.tag.dto.response.TagResponse;

// TODO: [추후] Tag 리팩터링 시 TagDto로 변경 필요함
public record AdateDto(
	Long id,
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
	String etag,
	boolean reminders,
	TagResponse tag
) {
}
