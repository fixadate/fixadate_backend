package com.fixadate.domain.adate.dto.response;

import java.time.LocalDateTime;

import com.fixadate.domain.tag.dto.response.TagResponse;

// TODO: [질문] 연동규격서에서는 tagResponse가 없던데 괜찮은가요?
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
