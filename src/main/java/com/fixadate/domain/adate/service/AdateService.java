package com.fixadate.domain.adate.service;

import static com.fixadate.domain.adate.mapper.AdateMapper.toAdateDto;
import static com.fixadate.domain.adate.mapper.AdateMapper.toEntity;
import static com.fixadate.global.exception.ExceptionCode.FORBIDDEN_UPDATE_ADATE;
import static com.fixadate.global.exception.ExceptionCode.FORBIDDEN_UPDATE_TODO;
import static com.fixadate.global.exception.ExceptionCode.INVALID_START_END_TIME;
import static com.fixadate.global.exception.ExceptionCode.NOT_FOUND_ADATE_CALENDAR_ID;
import static com.fixadate.global.exception.ExceptionCode.NOT_FOUND_TO_DO_ID;
import static com.fixadate.global.util.TimeUtil.getLocalDateTimeFromLocalDate;
import static com.fixadate.global.util.TimeUtil.getLocalDateTimeFromYearAndMonth;
import static com.fixadate.global.util.constant.ConstantValue.ADATE_WITH_COLON;

import com.fixadate.domain.adate.dto.ToDoRegisterDto;
import com.fixadate.domain.adate.dto.request.ToDoStatusUpdateRequest;
import com.fixadate.domain.adate.dto.response.AdateInfoResponse;
import com.fixadate.domain.adate.dto.response.AdateInfoResponse.DailyAdateInfo;
import com.fixadate.domain.adate.dto.response.AdateResponse;
import com.fixadate.domain.adate.entity.ToDo;
import com.fixadate.domain.adate.entity.ToDoStatus;
import com.fixadate.domain.adate.service.repository.ToDoRepository;
import com.fixadate.domain.main.dto.TodoInfo;
import com.fixadate.domain.member.entity.MemberPlans;
import com.fixadate.domain.member.entity.MemberResources;
import com.fixadate.domain.member.entity.Plans;
import com.fixadate.domain.member.entity.ResourceType;
import com.fixadate.domain.member.service.repository.MemberResourcesRepository;
import com.fixadate.domain.member.service.repository.PlanResourcesRepository;
import com.fixadate.global.exception.forbidden.ForbiddenException;
import com.fixadate.global.exception.notfound.NotFoundException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fixadate.domain.adate.dto.AdateDto;
import com.fixadate.domain.adate.dto.AdateRegisterDto;
import com.fixadate.domain.adate.dto.AdateUpdateDto;
import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.mapper.AdateMapper;
import com.fixadate.domain.adate.service.repository.AdateRepository;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.tag.event.object.TagSettingEvent;
import com.fixadate.global.exception.badrequest.InvalidTimeException;
import com.fixadate.global.exception.forbidden.AdateUpdateForbiddenException;
import com.fixadate.global.exception.notfound.AdateNotFoundException;
import com.fixadate.global.exception.notfound.TagNotFoundException;
import com.fixadate.global.facade.RedisFacade;
import com.fixadate.global.util.constant.ExternalCalendar;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdateService {

	private static final int ADATE_REDIS_DURATION = 20;

	private final AdateRepository adateRepository;
	private final RedisFacade redisFacade;
	private final ApplicationEventPublisher applicationEventPublisher;
	private final PlanResourcesRepository planResourcesRepository;
	private final MemberResourcesRepository memberResourcesRepository;
	private final ToDoRepository toDoRepository;
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
	DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

	@Transactional(noRollbackFor = TagNotFoundException.class)
	public AdateDto registerAdate(final AdateRegisterDto adateRegisterDto, final Member member) {
//		MemberResources foundMemberResources = checkAdateResourceCntExceedAndGetMemberResources(member);

		final Adate adate = toEntity(adateRegisterDto, member);
		final Adate savedAdate = adateRepository.save(adate);

		final String tagName = adateRegisterDto.tagName();
		if (tagName != null && !tagName.isEmpty()) {
			applicationEventPublisher.publishEvent(new TagSettingEvent(adate, tagName));
		}
//		foundMemberResources.plusResources(ResourceType.ADATE, 1);
		return AdateMapper.toAdateDto(savedAdate);
	}

	@Transactional(noRollbackFor = TagNotFoundException.class)
	public void registerExternalCalendarToAdate(final Adate adate, final ExternalCalendar externalCalendar) {
		Member foundMember = adate.getMember();
		MemberResources foundMemberResources = checkAdateResourceCntExceedAndGetMemberResources(foundMember);

		final Adate saveAdate = adateRepository.save(adate);

		foundMemberResources.plusResources(ResourceType.ADATE, 1);

		applicationEventPublisher.publishEvent(new TagSettingEvent(saveAdate, externalCalendar.getTagName()));
	}

	@Transactional
	public AdateDto restoreAdateByCalendarId(final String calendarId) {
		final String key = ADATE_WITH_COLON.getValue() + calendarId;
		final Adate adate = redisFacade.getAndDeleteObjectRedis(key, Adate.class);

		Member foundMember = adate.getMember();
		MemberResources foundMemberResources = checkAdateResourceCntExceedAndGetMemberResources(foundMember);

		final Adate saveAdate = adateRepository.save(adate);

		foundMemberResources.plusResources(ResourceType.ADATE, 1);

		return toAdateDto(saveAdate);
	}

	@Transactional(readOnly = true)
	public Optional<Adate> getAdateByCalendarId(final String calendarId) {
		return adateRepository.findAdateByCalendarId(calendarId);
	}

	// TODO: [추후] 회원정보를 받아, 해당 회원의 일정인지 확인 필요
	@Transactional(readOnly = true)
	public AdateDto getAdateInformationByCalendarId(final String calendarId) {
		final Adate adate = getAdateByCalendarId(calendarId).orElseThrow(
			() -> new AdateNotFoundException(NOT_FOUND_ADATE_CALENDAR_ID)
		);

		return toAdateDto(adate);
	}

	@Transactional(readOnly = true)
	public AdateInfoResponse getAdateByStartAndEndTime(
		final Member member,
		final LocalDateTime startDateTime,
		final LocalDateTime endDateTime
	) {
		checkStartAndEndTime(startDateTime, endDateTime);
		final List<Adate> adateList = adateRepository.findByDateRange(member, startDateTime, endDateTime);
		List<AdateResponse> adateInfos = adateList.stream().map(AdateResponse::of).toList();
		final List<ToDo> todoList = toDoRepository.findByMemberAndBetweenDates(member, startDateTime.toLocalDate(), endDateTime.toLocalDate());
		List<TodoInfo> todoInfos = todoList.stream().map(TodoInfo::of).toList();

		AdateInfoResponse dateInfos = new AdateInfoResponse();
		dateInfos.setDateInfos(startDateTime, endDateTime);

		for(DailyAdateInfo dateInfo : dateInfos.getDateList()) {
			List<AdateResponse> adateInfosByDate = new ArrayList<>();
			for(AdateResponse adateInfo : adateInfos) {
				// 포함된 일정이라면 출력
				if(adateInfo.startDate().equals(dateInfo.getDate()) ||
					(LocalDate.parse(dateInfo.getDate(), dateFormatter).atStartOfDay().isAfter(LocalDateTime.parse(adateInfo.startsWhen(), formatter)))
					&& LocalDateTime.parse(adateInfo.endsWhen(), formatter).isAfter(LocalDate.parse(dateInfo.getDate(), dateFormatter).atStartOfDay())) {
					adateInfosByDate.add(adateInfo);
				}
			}

			dateInfo.setAdateInfoList(adateInfosByDate);

			List<TodoInfo> todoInfosByDate = new ArrayList<>();
			for(TodoInfo todoInfo : todoInfos) {
				if(todoInfo.date().equals(dateInfo.getDate())) {
					todoInfosByDate.add(todoInfo);
				}
			}
			dateInfo.setTodoInfoList(todoInfosByDate);
			dateInfo.setTotalCnt();
		}

		return dateInfos;
	}

	private void checkStartAndEndTime(final LocalDateTime startTime, final LocalDateTime endTime) {
		if (startTime.isAfter(endTime)) {
			throw new InvalidTimeException(INVALID_START_END_TIME);
		}
	}

	@Transactional(readOnly = true)
	public AdateInfoResponse getAdatesByMonth(final Member member, final int year, final int month) {
		final LocalDateTime startTime = getLocalDateTimeFromYearAndMonth(year, month, true);
		final LocalDateTime endTime = getLocalDateTimeFromYearAndMonth(year, month, false);

		return getAdateByStartAndEndTime(member, startTime, endTime);
	}

	@Transactional(readOnly = true)
	public List<AdateDto> getAdatesByDate(
		final Member member,
		final LocalDate firstDay,
		final LocalDate lastDay
	) {
		final LocalDateTime startTime = getLocalDateTimeFromLocalDate(firstDay, true);
		final LocalDateTime endTime = getLocalDateTimeFromLocalDate(lastDay, false);

//		return getAdateByStartAndEndTime(member, startTime, endTime);
		return new ArrayList<>();
	}
	
	@Transactional(noRollbackFor = TagNotFoundException.class)
	public AdateDto updateAdate(
		final Member member,
		final String calendarId,
		final AdateUpdateDto adateUpdateDto
	) {
		final Adate adate = getAdateByCalendarId(calendarId).orElseThrow(
			() -> new AdateNotFoundException(NOT_FOUND_ADATE_CALENDAR_ID)
		);

		validateUserCanUpdate(adate, member);
		updateIfNotNull(adate, adateUpdateDto);

		return toAdateDto(adate);
	}

	private void validateUserCanUpdate(final Adate adate, final Member member) {
		if (!adate.isOwner(member)) {
			throw new AdateUpdateForbiddenException(FORBIDDEN_UPDATE_ADATE);
		}
	}

	private void updateIfNotNull(final Adate adate, final AdateUpdateDto adateUpdateDto) {
		if (adateUpdateDto.tagName() != null) {
			applicationEventPublisher.publishEvent(new TagSettingEvent(adate, adateUpdateDto.tagName()));
		}
		if (adateUpdateDto.title() != null) {
			adate.updateTitle(adateUpdateDto.title());
		}
		if (adateUpdateDto.notes() != null) {
			adate.updateNotes(adateUpdateDto.notes());
		}
		if (adateUpdateDto.location() != null) {
			adate.updateLocation(adateUpdateDto.location());
		}
		if (adateUpdateDto.alertWhen() != null) {
			adate.updateAlertWhen(adateUpdateDto.alertWhen());
		}
		if (adateUpdateDto.repeatFreq() != null) {
			adate.updateRepeatFreq(adateUpdateDto.repeatFreq());
		}

		adate.updateIfAllDay(adateUpdateDto.ifAllDay());
		adate.updateStartsWhen(adateUpdateDto.startsWhen());
		adate.updateEndsWhen(adateUpdateDto.endsWhen());
		adate.updateReminders(adateUpdateDto.reminders());
	}

	@Transactional
	public void removeAdate(final Adate adate) {
		Member member = adate.getMember();
		adateRepository.delete(adate);

		// todo: adate 삭제 상태 체크
		minusAdateResource(member);
	}

	@Transactional
	public void removeAdateByCalendarId(final String calendarId) {
		final Adate adate = getAdateByCalendarId(calendarId).orElseThrow(
			() -> new AdateNotFoundException(NOT_FOUND_ADATE_CALENDAR_ID)
		);
		Member member = adate.getMember();
		adateRepository.delete(adate);

		final Duration adateDuration = Duration.ofDays(ADATE_REDIS_DURATION);
		redisFacade.removeAndRegisterObject(ADATE_WITH_COLON.getValue() + calendarId, adate, adateDuration);

		// todo: adate 삭제 상태 체크
		minusAdateResource(member);
	}

	private void minusAdateResource(Member member) {
		MemberPlans memberPlan = member.getMemberPlan();
		if(memberPlan.isValid()){
			throw new RuntimeException("member plan invalid");
		}
		MemberResources foundMemberResources = memberResourcesRepository.getMemberResources(member);
		foundMemberResources.minusResources(ResourceType.ADATE, 1);
	}

	private MemberResources checkAdateResourceCntExceedAndGetMemberResources(Member member) {
		MemberPlans memberPlan = member.getMemberPlan();
		if(memberPlan.isValid()){
			throw new RuntimeException("member plan invalid");
		}
		Plans foundPlan = memberPlan.getPlan();
		int adateResourceMaxCnt = planResourcesRepository.getResourceMaxCnt(ResourceType.ADATE, foundPlan);
		MemberResources foundMemberResources = memberResourcesRepository.getMemberResources(member);
		int currentAdateResourceCnt = foundMemberResources.getResourceCnt(ResourceType.ADATE);
		if(currentAdateResourceCnt + 1 > adateResourceMaxCnt){
			throw new RuntimeException("adate resource limit exceeded");
		}
		return foundMemberResources;
	}

	@Transactional
	public ToDo registerToDo(ToDoRegisterDto toDoRegisterDto) {
		final ToDo toDo = toEntity(toDoRegisterDto);
		final ToDo savedToDo = toDoRepository.save(toDo);
		return savedToDo;
	}

	@Transactional
	public ToDo updateToDoStatus(ToDoStatusUpdateRequest toDoStatusUpdateRequest) {
		ToDoStatus toDoStatus = ToDoStatus.translateStringToStatus(toDoStatusUpdateRequest.getToDoStatus());
		ToDo toDo = toDoRepository.findToDoByToDoId(toDoStatusUpdateRequest.getId()).orElseThrow();
		toDo.updateToDoStatus(toDoStatus);
		return toDo;
	}

	@Transactional
	public void updateToDoTitle(ToDo toDo, String title) {
		toDo.updateTitle(title);
	}

	@Transactional
	public ToDo deleteToDo(String toDoId) {
		ToDo toDo = toDoRepository.findToDoByToDoId(Long.parseLong(toDoId)).orElseThrow();
		toDoRepository.delete(toDo);
		return toDo;
	}

	public ToDo getTodo(Long id, Member member) {
		ToDo toDo = toDoRepository.findToDoByToDoId(id).orElseThrow(
			() -> new NotFoundException(NOT_FOUND_TO_DO_ID)
		);
		if (!toDo.getMember().equals(member)) {
			throw new ForbiddenException(FORBIDDEN_UPDATE_TODO);
		}
		return toDo;
	}

	public List<ToDo> getToDoByDate(String date, Member member) {
		LocalDate dateToRetrieve = LocalDate.parse(date);
		List<ToDo> ToDos = toDoRepository.findByMemberAndBetweenDates(member, dateToRetrieve, dateToRetrieve);
		return ToDos;
	}
}
