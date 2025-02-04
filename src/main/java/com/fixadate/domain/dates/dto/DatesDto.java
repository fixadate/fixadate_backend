package com.fixadate.domain.dates.dto;

import com.fixadate.domain.tag.dto.response.TagResponse;
import java.time.LocalDateTime;

// TODO: [추후] Tag 리팩터링 시 TagDto로 변경 필요함
public record DatesDto(
	Long id,
	String title,
	String notes,
	String location,
	LocalDateTime alertWhen,
	LocalDateTime repeatFreq,
	boolean ifAllDay,
	LocalDateTime startsWhen,
	LocalDateTime endsWhen,
	String etag,
	boolean reminders,
	TagResponse tag
) {
}