package com.fixadate.domain.main.dto;

import com.fixadate.domain.dates.entity.Dates;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record DatesInfo(
	String calendarId,
	String title,
	String note,
	TagInfo tag,
	String startDate,
	String startsWhen,
	String endsWhen,
	List<DatesMemberInfo> teamMemberList
	){
		public static DatesInfo of(Dates dates, List<DatesMemberInfo> teamMemberList) {
			return new DatesInfo(
				dates.getCalendarId(),
				dates.getTitle(),
				dates.getNotes(),
				null,
				dates.getStartsWhen().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
				dates.getStartsWhen().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")),
				dates.getEndsWhen().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")),
				teamMemberList
			);
		}
	}
