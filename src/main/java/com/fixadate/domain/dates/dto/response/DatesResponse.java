package com.fixadate.domain.dates.dto.response;

import com.fixadate.domain.dates.entity.Dates;
import com.fixadate.domain.main.dto.DatesMemberInfo;
import com.fixadate.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record DatesResponse(
	@Schema(description = "제안자 여부")
	boolean isProponent,
	String title,
	String notes,
	String startDate,
	String startsWhen,
	String endsWhen,
	String calendarId,
	Long teamId,
	List<DatesMemberInfo> datesMemberList
) {
	public static DatesResponse of(Member member, Dates dates, List<DatesMemberInfo> datesMemberList) {
		return new DatesResponse(
			dates.isProponent(member),
			dates.getTitle(),
			dates.getNotes(),
			dates.getStartsWhen().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
			dates.getStartsWhen().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")),
			dates.getEndsWhen().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")),
			dates.getCalendarId(),
			dates.getTeam().getId(),
			datesMemberList
		);
	}
}
