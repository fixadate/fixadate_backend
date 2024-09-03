package com.fixadate.domain.adate.controller.impl;

import static com.fixadate.global.exception.ExceptionCode.INVALID_START_END_TIME;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
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
import com.fixadate.domain.adate.dto.AdateDto;
import com.fixadate.domain.adate.dto.AdateRegisterDto;
import com.fixadate.domain.adate.dto.AdateUpdateDto;
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
	public ResponseEntity<AdateRegisterRequest> registerAdate(
		@Valid @RequestBody final AdateRegisterRequest adateRegisterRequest,
		@AuthenticationPrincipal final MemberPrincipal memberPrincipal
	) {
		final Member member = memberPrincipal.getMember();
		final AdateRegisterDto adateRegisterDto = AdateMapper.toAdateRegisterDto(adateRegisterRequest);
		adateService.registerAdate(adateRegisterDto, member);

		// TODO: [질문] service에서 save한 member 객체로 응답값을 만들어 반환하는 것이 더 적절할까요?
		return ResponseEntity.ok(adateRegisterRequest);
	}

	@Override
	@PostMapping("/restore/{calendarId}")
	public ResponseEntity<AdateResponse> restoreAdate(@PathVariable final String calendarId) {
		final AdateDto adate = adateService.restoreAdateByCalendarId(calendarId);
		final AdateResponse response = AdateMapper.toAdateResponse(adate);

		return ResponseEntity.ok(response);
	}

	@Override
	@GetMapping("/{calendarId}")
	public ResponseEntity<AdateResponse> getAdate(@PathVariable final String calendarId) {
		final AdateDto adate = adateService.getAdateInformationByCalendarId(calendarId);
		final AdateResponse response = AdateMapper.toAdateResponse(adate);

		return ResponseEntity.ok(response);
	}

	@Override
	@GetMapping
	public ResponseEntity<List<AdateViewResponse>> getAdatesBy(
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

		return ResponseEntity.ok(responses);
	}

	private void checkDateTime(final LocalDateTime startDateTime, final LocalDateTime endDateTime) {
		if (startDateTime.isAfter(endDateTime)) {
			throw new InvalidTimeException(INVALID_START_END_TIME);
		}
	}

	@Override
	@GetMapping("/month")
	public ResponseEntity<List<AdateViewResponse>> getAdatesBy(
		@AuthenticationPrincipal final MemberPrincipal memberPrincipal,
		@RequestParam final int year,
		@RequestParam @Min(1) @Max(12) final int month
	) {
		final Member member = memberPrincipal.getMember();
		final List<AdateDto> adates = adateService.getAdatesByMonth(member, year, month);
		final List<AdateViewResponse> responses = adates.stream()
														.map(AdateMapper::toAdateViewResponse)
														.toList();

		return ResponseEntity.ok(responses);
	}

	@Override
	@GetMapping("/day")
	public ResponseEntity<List<AdateViewResponse>> getAdatesBy(
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

		return ResponseEntity.ok(responses);
	}

	private void checkDate(final LocalDate firstDay, final LocalDate lastDay) {
		if (firstDay.isAfter(lastDay)) {
			throw new InvalidTimeException(INVALID_START_END_TIME);
		}
	}

	@Override
	@PatchMapping("/{calendarId}")
	public ResponseEntity<AdateResponse> updateAdate(
		@PathVariable final String calendarId,
		@Valid @RequestBody final AdateUpdateRequest adateUpdateRequest,
		@AuthenticationPrincipal final MemberPrincipal memberPrincipal
	) {
		final Member member = memberPrincipal.getMember();
		final AdateUpdateDto adateUpdateDto = AdateMapper.toAdateUpdateDto(adateUpdateRequest);
		final AdateDto adate = adateService.updateAdate(member, calendarId, adateUpdateDto);
		final AdateResponse response = AdateMapper.toAdateResponse(adate);

		return ResponseEntity.ok(response);
	}

	@Override
	@DeleteMapping("/{calendarId}")
	public ResponseEntity<Void> removeAdate(@PathVariable final String calendarId) {
		adateService.removeAdateByCalendarId(calendarId);

		return ResponseEntity.noContent()
							 .build();
	}
}
