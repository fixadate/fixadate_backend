package com.fixadate.domain.main.dto;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.dates.entity.Dates;
import com.fixadate.domain.main.dto.response.MainInfoResponse.DateInfo;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public record Schedule(
	Long id,
	String title,
	String note,
	TagInfo tag,
	String startDate,
	String startsWhen,
	String endsWhen,
	List<DatesMemberInfo> teamMemberList,
	boolean isAdate
	// todo: 반복여부
	){
	public static Schedule of(AdateInfo adateInfo) {
		return new Schedule(
			adateInfo.id(),
			adateInfo.title(),
			adateInfo.note(),
			adateInfo.tag(),
			adateInfo.startDate(),
			adateInfo.startsWhen(),
			adateInfo.endsWhen(),
			new ArrayList<>(),
			true
		);
	}
		public static Schedule of(DatesInfo datesInfo) {
			return new Schedule(
				datesInfo.id(),
				datesInfo.title(),
				datesInfo.note(),
				datesInfo.tag(),
				datesInfo.startDate(),
				datesInfo.startsWhen(),
				datesInfo.endsWhen(),
				datesInfo.teamMemberList(),
				false
			);
		}
	}
