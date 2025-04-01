package com.fixadate.domain.dates.dto;

import com.fixadate.domain.tag.dto.response.TagResponse;
import java.time.LocalDateTime;

public record DatesDto(
	String proponentId,
	String calendarId,
	String title,
	String notes,
	LocalDateTime startsWhen,
	LocalDateTime endsWhen){
}