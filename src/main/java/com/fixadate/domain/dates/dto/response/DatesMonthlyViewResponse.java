package com.fixadate.domain.dates.dto.response;

import com.fixadate.domain.dates.entity.Grades;
import java.util.List;

public record DatesMonthlyViewResponse(
	String yearMonth,
	List<Day> days
) {
	public record Team(
		Long teamId,
		Grades grades
	){
	}
	public record Day(
		String date,
		String datesCnt,
		List<Dates> datesList
	){}

	public record Dates(
		String title,
		String startsWhen,
		String endsWhen,
		boolean isMySchedule,
		int memberCnt,
		List<DatesParticipants> datesParticipants
	){}

	public record DatesParticipants(
		String name,
		String profileUrl
	){}
}
