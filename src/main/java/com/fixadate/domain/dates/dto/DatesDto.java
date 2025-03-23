package com.fixadate.domain.dates.dto;

import com.fixadate.domain.tag.dto.response.TagResponse;
import java.time.LocalDateTime;

// TODO: [추후] Tag 리팩터링 시 TagDto로 변경 필요함
public record DatesDto(
	String calendarId,
	String title,
	String notes,
	LocalDateTime startsWhen,
	LocalDateTime endsWhen){
}