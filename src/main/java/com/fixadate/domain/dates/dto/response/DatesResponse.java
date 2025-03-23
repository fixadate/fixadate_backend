package com.fixadate.domain.dates.dto.response;

import com.fixadate.domain.dates.entity.Dates;
import com.fixadate.domain.main.dto.DatesInfo;
import com.fixadate.domain.main.dto.DatesMemberInfo;
import com.fixadate.domain.main.dto.TagInfo;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record DatesResponse(
	String title,
	String notes,
	String startDate,
	String startsWhen,
	String endsWhen,
	String calendarId,
	List<DatesMemberInfo> teamMemberList
) {
	public static DatesResponse of(Dates dates, List<DatesMemberInfo> teamMemberList) {
		return new DatesResponse(
			dates.getTitle(),
			dates.getNotes(),
			dates.getStartsWhen().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
			dates.getStartsWhen().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")),
			dates.getEndsWhen().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")),
			dates.getCalendarId(),
			teamMemberList
		);
	}
}
