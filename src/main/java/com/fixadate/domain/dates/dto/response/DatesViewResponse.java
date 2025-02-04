package com.fixadate.domain.dates.dto.response;

import com.fixadate.domain.tag.dto.response.TagResponse;
import java.time.LocalDateTime;

public record DatesViewResponse(
	String title,
	String notes,
	boolean ifAllDay,
	LocalDateTime startsWhen,
	LocalDateTime endsWhen,
	TagResponse tagResponse
) {
}