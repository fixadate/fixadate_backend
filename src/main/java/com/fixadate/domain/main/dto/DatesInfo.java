package com.fixadate.domain.main.dto;

import com.fixadate.domain.dates.entity.Dates;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record DatesInfo(
	Long id,
	String title,
	String note,
	TagInfo tag,
	String startDate,
	String startsWhen,
	String endsWhen,
	List<DatesMemberInfo> teamMemberList
	// todo: 반복여부
	){
		public static DatesInfo of(Dates dates, List<DatesMemberInfo> teamMemberList) {
			return new DatesInfo(
				dates.getId(),
				dates.getTitle(),
				dates.getNotes(),
				new TagInfo(
					dates.getTag().getId(),
					dates.getTag().getColor(),
					dates.getTag().getName()
				),
				dates.getStartsWhen().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
				dates.getStartsWhen().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")),
				dates.getEndsWhen().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")),
				teamMemberList
			);
		}
	}
