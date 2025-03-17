package com.fixadate.domain.main.dto;

import com.fixadate.domain.adate.dto.response.AdateResponse;
import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.dates.dto.response.DatesResponse;
import com.fixadate.domain.dates.entity.Dates;
import com.fixadate.domain.main.dto.response.MainInfoResponse.DateInfo;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public record Schedule(
	String calendarId,
	String title,
	String note,
	String tagName,
	String tagColor,
	String startDate,
	String startsWhen,
	String endsWhen,
	List<DatesMemberInfo> teamMemberList,
	boolean isAdate
	// todo: 반복여부
	){
	public static Schedule of(AdateResponse adateInfo) {
		return new Schedule(
			adateInfo.calendarId(),
			adateInfo.title(),
			adateInfo.notes(),
			adateInfo.tagName(),
			adateInfo.tagColor(),
			adateInfo.startDate(),
			adateInfo.startsWhen(),
			adateInfo.endsWhen(),
			new ArrayList<>(),
			true
		);
	}
		public static Schedule of(DatesResponse datesInfo) {
			return new Schedule(
				datesInfo.calendarId(),
				datesInfo.title(),
				datesInfo.notes(),
				"",
				"",
				datesInfo.startDate(),
				datesInfo.startsWhen(),
				datesInfo.endsWhen(),
				datesInfo.teamMemberList(),
				false
			);
		}
	}
