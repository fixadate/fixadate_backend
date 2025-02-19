package com.fixadate.domain.adate.controller.impl;

import static com.fixadate.global.exception.ExceptionCode.INVALID_START_END_TIME;

import com.fixadate.domain.adate.dto.*;
import com.fixadate.domain.adate.dto.request.ToDoStatusUpdateRequest;
import com.fixadate.domain.adate.dto.request.TodoRegisterRequest;
import com.fixadate.domain.adate.dto.response.ToDoResponse;
import com.fixadate.domain.adate.entity.ToDo;
import com.fixadate.domain.adate.entity.ToDoStatus;
import com.fixadate.global.dto.GeneralResponseDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.fixadate.domain.adate.controller.AdateController;
import com.fixadate.domain.adate.dto.request.AdateRegisterRequest;
import com.fixadate.domain.adate.dto.request.AdateUpdateRequest;
import com.fixadate.domain.adate.dto.response.AdateResponse;
import com.fixadate.domain.adate.dto.response.AdateViewResponse;
import com.fixadate.domain.adate.mapper.AdateMapper;
import com.fixadate.domain.adate.service.AdateService;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.annotation.RestControllerWithMapping;
import com.fixadate.global.exception.badrequest.InvalidTimeException;
import com.fixadate.global.jwt.MemberPrincipal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestControllerWithMapping("/v1/calendar")
@Validated
@RequiredArgsConstructor
public class AdateControllerImpl implements AdateController {

	private final AdateService adateService;

	@Override
	@PostMapping
	public GeneralResponseDto registerAdate(
		@Valid @RequestBody final AdateRegisterRequest adateRegisterRequest,
		@AuthenticationPrincipal final MemberPrincipal memberPrincipal
	) {
		final Member member = memberPrincipal.getMember();
		final AdateRegisterDto adateRegisterDto = AdateMapper.toAdateRegisterDto(adateRegisterRequest);
		final AdateDto adate = adateService.registerAdate(adateRegisterDto, member);
		final AdateResponse response = AdateMapper.toAdateResponse(adate);

		return GeneralResponseDto.success("", response);
	}

	@Override
	@PostMapping("/restore/{calendarId}")
	public GeneralResponseDto restoreAdate(@PathVariable final String calendarId) {
		final AdateDto adate = adateService.restoreAdateByCalendarId(calendarId);
		final AdateResponse response = AdateMapper.toAdateResponse(adate);

		return GeneralResponseDto.success("", response);
	}

	@Override
	@GetMapping("/{calendarId}")
	public GeneralResponseDto getAdate(@PathVariable final String calendarId) {
		final AdateDto adate = adateService.getAdateInformationByCalendarId(calendarId);
		final AdateResponse response = AdateMapper.toAdateResponse(adate);

		return GeneralResponseDto.success("", response);
	}

	@Override
	@GetMapping
	public GeneralResponseDto getAdatesBy(
		@AuthenticationPrincipal final MemberPrincipal memberPrincipal,
		@RequestParam final LocalDateTime startDateTime,
		@RequestParam final LocalDateTime endDateTime
	) {
		checkDateTime(startDateTime, endDateTime);

		final Member member = memberPrincipal.getMember();
		final List<AdateDto> adates = adateService.getAdateByStartAndEndTime(member, startDateTime, endDateTime);
		final List<AdateViewResponse> responses = adates.stream()
														.map(AdateMapper::toAdateViewResponse)
														.toList();

		return GeneralResponseDto.success("", responses);
	}

	private void checkDateTime(final LocalDateTime startDateTime, final LocalDateTime endDateTime) {
		if (startDateTime.isAfter(endDateTime)) {
			throw new InvalidTimeException(INVALID_START_END_TIME);
		}
	}

	@Override
	@GetMapping("/month")
	public GeneralResponseDto getAdatesBy(
		@AuthenticationPrincipal final MemberPrincipal memberPrincipal,
		@RequestParam final int year,
		@RequestParam @Min(1) @Max(12) final int month
	) {
		final Member member = memberPrincipal.getMember();
		final List<AdateDto> adates = adateService.getAdatesByMonth(member, year, month);
		final List<AdateViewResponse> responses = adates.stream()
														.map(AdateMapper::toAdateViewResponse)
														.toList();

		return GeneralResponseDto.success("", responses);
	}

	@Override
	@GetMapping("/day")
	public GeneralResponseDto getAdatesBy(
		@AuthenticationPrincipal final MemberPrincipal memberPrincipal,
		@RequestParam final LocalDate firstDay,
		@RequestParam final LocalDate lastDay
	) {
		checkDate(firstDay, lastDay);

		final Member member = memberPrincipal.getMember();
		final List<AdateDto> adates = adateService.getAdatesByDate(member, firstDay, lastDay);
		final List<AdateViewResponse> responses = adates.stream()
														.map(AdateMapper::toAdateViewResponse)
														.toList();

		return GeneralResponseDto.success("", responses);
	}

	private void checkDate(final LocalDate firstDay, final LocalDate lastDay) {
		if (firstDay.isAfter(lastDay)) {
			throw new InvalidTimeException(INVALID_START_END_TIME);
		}
	}

	@Override
	@PatchMapping("/{calendarId}")
	public GeneralResponseDto updateAdate(
		@PathVariable final String calendarId,
		@Valid @RequestBody final AdateUpdateRequest adateUpdateRequest,
		@AuthenticationPrincipal final MemberPrincipal memberPrincipal
	) {
		final Member member = memberPrincipal.getMember();
		final AdateUpdateDto adateUpdateDto = AdateMapper.toAdateUpdateDto(adateUpdateRequest);
		final AdateDto adate = adateService.updateAdate(member, calendarId, adateUpdateDto);
		final AdateResponse response = AdateMapper.toAdateResponse(adate);

		return GeneralResponseDto.success("",response);
	}

	@Override
	@DeleteMapping("/{calendarId}")
	public GeneralResponseDto removeAdate(@PathVariable final String calendarId) {
		adateService.removeAdateByCalendarId(calendarId);

		return GeneralResponseDto.success("", "");
	}

	@Override
	@PostMapping("/todo")
	public GeneralResponseDto registerToDo(
		@Valid @RequestBody final TodoRegisterRequest todoRegisterRequest,
		@AuthenticationPrincipal final MemberPrincipal memberPrincipal
	) {
			final Member member = memberPrincipal.getMember();
			final ToDoRegisterDto toDoRegisterDto = AdateMapper.toToDoRegisterDto(todoRegisterRequest);
			final ToDo toDo = adateService.registerToDo(toDoRegisterDto, member);
			final ToDoResponse toDoResponse = AdateMapper.toToDoResponse(toDo);
			return GeneralResponseDto.success("", toDoResponse);
	}

	@Override
	@PostMapping("/todo/status")
	public GeneralResponseDto updateToDoStatus(
			@Valid @RequestBody final ToDoStatusUpdateRequest toDoStatusUpdateRequest,
			@AuthenticationPrincipal final MemberPrincipal memberPrincipal
	) {


		final ToDo toDo = adateService.updateToDoStatus(toDoStatusUpdateRequest);
		return GeneralResponseDto.success("", toDo);
	}

	@Override
	@GetMapping("/todo/{toDoId}")
	public GeneralResponseDto deleteToDo(@PathVariable final String toDoId, MemberPrincipal memberPrincipal) {
		ToDo toDo = adateService.deleteToDo(toDoId);
		return GeneralResponseDto.success("", toDo);
	}
}
