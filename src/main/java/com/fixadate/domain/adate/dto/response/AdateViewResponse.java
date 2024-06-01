package com.fixadate.domain.adate.dto.response;

import java.time.LocalDateTime;

/**
 *
 * @author yongjunhong
 * @since 2024. 6. 1.
 */
public record AdateViewResponse(
	String title,
	String color,
	boolean ifAllDay,
	LocalDateTime startsWhen,
	LocalDateTime endsWhen,
	String calendarId) {
}
