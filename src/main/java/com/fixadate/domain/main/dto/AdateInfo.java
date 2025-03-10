package com.fixadate.domain.main.dto;

import com.fixadate.domain.adate.entity.Adate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record AdateInfo(
	Long id,
	String title,
	String note,
	TagInfo tag,
	String startDate,
	String startsWhen,
	String endsWhen
	// todo: 반복여부
	){
		public static AdateInfo of(Adate adate) {
			return new AdateInfo(
				adate.getId(),
				adate.getTitle(),
				adate.getNotes(),
				new TagInfo(
					adate.getTag().getId(),
					adate.getTag().getColor(),
					adate.getTag().getName()
				),
				adate.getStartsWhen().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
				adate.getStartsWhen().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")),
				adate.getEndsWhen().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"))
			);
		}
	}
