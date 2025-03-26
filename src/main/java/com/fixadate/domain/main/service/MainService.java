package com.fixadate.domain.main.service;

import com.fixadate.domain.adate.dto.response.AdateResponse;
import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.entity.ToDo;
import com.fixadate.domain.adate.service.repository.AdateRepository;
import com.fixadate.domain.adate.service.repository.ToDoRepository;
import com.fixadate.domain.auth.entity.BaseEntity.DataStatus;
import com.fixadate.domain.dates.dto.response.DatesResponse;
import com.fixadate.domain.dates.entity.Dates;
import com.fixadate.domain.dates.entity.DatesMembers;
import com.fixadate.domain.dates.repository.DatesMembersRepository;
import com.fixadate.domain.dates.repository.DatesRepository;
import com.fixadate.domain.main.dto.AdateInfo;
import com.fixadate.domain.main.dto.DatesInfo;
import com.fixadate.domain.main.dto.DatesMemberInfo;
import com.fixadate.domain.main.dto.Schedule;
import com.fixadate.domain.main.dto.TodoInfo;
import com.fixadate.domain.main.dto.response.MainInfoResponse;
import com.fixadate.domain.main.dto.response.MainInfoResponse.DateInfo;
import com.fixadate.domain.member.entity.Member;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MainService {

	private final AdateRepository adateRepository;
	private final ToDoRepository toDoRepository;
	private final DatesRepository datesRepository;
	private final DatesMembersRepository datesMembersRepository;

	public MainInfoResponse getMainInfo(Member member, String yyyyMM, int weekNum) {
		MainInfoResponse dateInfos = new MainInfoResponse();

		// yyyyMM와 weekNum을 통해 날짜 생성
		LocalDateTime firstDayDateTime = getLocalDateTimeFromYyyyMMAndWeek(yyyyMM, weekNum);
		LocalDateTime lastDayDateTime = firstDayDateTime.plusWeeks(1); // 00:00 이전이어야하기에
		dateInfos.setDateInfos(yyyyMM, weekNum, firstDayDateTime, lastDayDateTime);

		List<Adate> adateList = adateRepository.findByMemberAndBetweenDates(member, firstDayDateTime, lastDayDateTime);
		List<AdateResponse> adateInfos = adateList.stream().map(AdateResponse::of).toList();

		List<ToDo> todoList = toDoRepository.findByMemberAndBetweenDates(member, firstDayDateTime.toLocalDate(), lastDayDateTime.toLocalDate());
		List<TodoInfo> todoInfos = todoList.stream().map(TodoInfo::of).toList();

		List<Dates> datesList = datesRepository.findByMemberAndStartsWhenBetween(member, firstDayDateTime, lastDayDateTime);
		List<DatesResponse> dateInfosList = new ArrayList<>();
		String myMemberId = member.getId();

		for(Dates dates : datesList){
			List<DatesMembers> datesMembers = datesMembersRepository.findAllByDatesAndStatusIs(dates, DataStatus.ACTIVE);
			List<DatesMemberInfo> datesMemberList = datesMembers.stream().map(each -> DatesMemberInfo.of(each, myMemberId)).toList();
			DatesResponse datesInfo = DatesResponse.of(dates, datesMemberList);
			dateInfosList.add(datesInfo);
		}

		for(DateInfo dateInfo : dateInfos.getDateList()) {
			List<AdateResponse> adateInfosByDate = new ArrayList<>();
			for(AdateResponse adateInfo : adateInfos) {
				if(adateInfo.startDate().equals(dateInfo.getDate())) {
					adateInfosByDate.add(adateInfo);
				}
			}
			dateInfo.setAdateInfoList(adateInfosByDate);
			dateInfo.getScheduleList().addAll(adateInfosByDate.stream().map(Schedule::of).toList());

			List<TodoInfo> todoInfosByDate = new ArrayList<>();
			for(TodoInfo todoInfo : todoInfos) {
				if(todoInfo.date().equals(dateInfo.getDate())) {
					todoInfosByDate.add(todoInfo);
				}
			}
			dateInfo.setTodoInfoList(todoInfosByDate);

			List<DatesResponse> datesInfosByDate = new ArrayList<>();
			for(DatesResponse datesInfo : dateInfosList) {
				if(datesInfo.startDate().equals(dateInfo.getDate())) {
					datesInfosByDate.add(datesInfo);
				}
			}
			dateInfo.setDatesInfoList(datesInfosByDate);
			dateInfo.getScheduleList().addAll(datesInfosByDate.stream().map(Schedule::of).toList());

			// 오늘의 Schedule로 묶인거 정렬
			dateInfo.getScheduleList().sort(Comparator.comparing(schedule -> {
				String startsWhen = schedule.startsWhen();
				return LocalDateTime.parse(startsWhen, DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
			}));
		}

		return dateInfos;
	}

	// yyyyMM 형식의 날짜와 주차를 입력받아 해당 주차의 첫 번째 날짜를 LocalDateTime 형식으로 반환
	public static LocalDateTime getLocalDateTimeFromYyyyMMAndWeek(String yyyyMM, int week) {
		// yyyyMM 문자열을 파싱하여 월의 첫 번째 날을 얻음
		YearMonth yearMonth = YearMonth.parse(yyyyMM, DateTimeFormatter.ofPattern("yyyyMM"));
		LocalDate firstDayOfMonth = yearMonth.atDay(1);

		// 월의 첫 번째 날이 일요일인지 확인하고, 일요일이 아니면 가장 가까운 일요일로 이동
		LocalDate firstSunday = getFirstSunday(firstDayOfMonth);

		// 주차에 맞는 첫 번째 날 계산
		LocalDate firstDayOfWeek = firstSunday.plusWeeks(week - 1);

		return firstDayOfWeek.atStartOfDay();
	}

	private static LocalDate getFirstSunday(LocalDate date) {
		while (date.getDayOfWeek() != DayOfWeek.SUNDAY) {
			date = date.plusDays(1);
		}
		return date;
	}

    public String getStoryBoard(Member member) {
		return StringUtils.defaultString(member.getStoryBoard());
    }

	public String updateStoryBoard(Member member, String storyBoard) {
		member.updateStoryBoard(storyBoard);
		return StringUtils.defaultString(member.getStoryBoard());
	}
}
