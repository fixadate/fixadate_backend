package com.fixadate.domain.adate.controller.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import com.fixadate.global.jwt.MemberPrincipal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestControllerWithMapping("/v1/calendar")
@RequiredArgsConstructor
public class AdateControllerImpl implements AdateController {
	private final AdateService adateService;

	@Override
	@PostMapping()
	public ResponseEntity<AdateRegisterRequest> registerAdateEvent(
		@Valid @RequestBody AdateRegisterRequest adateRegisterRequest,
		@AuthenticationPrincipal MemberPrincipal memberPrincipal
	) {
		Member member = memberPrincipal.getMember();
		final AdateRegisterDto adateRegisterDto = AdateMapper.toDto(adateRegisterRequest);
		adateService.registerAdateEvent(adateRegisterDto, member);
		return ResponseEntity.ok(adateRegisterRequest);
	}

	@Override
	@GetMapping
	public ResponseEntity<List<AdateViewResponse>> getAdates(
		@AuthenticationPrincipal MemberPrincipal memberPrincipal,
		@RequestParam LocalDateTime startDateTime,
		@RequestParam LocalDateTime endDateTime
	) {
		Member member = memberPrincipal.getMember();
		List<AdateDto> adates = adateService.getAdateByStartAndEndTime(member, startDateTime, endDateTime);
		final List<AdateViewResponse> responses = adates.stream()
														.map(AdateMapper::toAdateViewResponse)
														.toList();

		return ResponseEntity.ok(responses);
	}

	@Override
	@PostMapping("/restore/{calendarId}")
	public ResponseEntity<AdateResponse> restoreAdate(@PathVariable String calendarId) {
		final AdateDto adate = adateService.restoreAdateByCalendarId(calendarId);
		final AdateResponse response = AdateMapper.toAdateResponse(adate);

		return ResponseEntity.ok(response);
	}

	@Override
	@GetMapping("/{calendarId}")
	public ResponseEntity<AdateResponse> getAdate(@PathVariable String calendarId) {
		final AdateDto adate = adateService.getAdateInformationByCalendarId(calendarId);
		final AdateResponse response = AdateMapper.toAdateResponse(adate);

		return ResponseEntity.ok(response);
	}

	@Override
	@PatchMapping("/{calendarId}")
	public ResponseEntity<AdateResponse> updateAdate(
		@PathVariable String calendarId,
		@Valid @RequestBody AdateUpdateRequest adateUpdateRequest,
		@AuthenticationPrincipal MemberPrincipal memberPrincipal
	) {
		final Member member = memberPrincipal.getMember();
		final AdateUpdateDto adateUpdateDto = AdateMapper.toDto(adateUpdateRequest);
		final AdateDto adate = adateService.updateAdate(member, calendarId, adateUpdateDto);
		final AdateResponse response = AdateMapper.toAdateResponse(adate);

		return ResponseEntity.ok(response);
	}

	@Override
	@DeleteMapping("/{calendarId}")
	public ResponseEntity<Void> removeAdate(@PathVariable String calendarId) {
		adateService.removeAdateByCalendarId(calendarId);
		return ResponseEntity.noContent().build();
	}

	// TODO: [질문] requestParam에 기본 값이 없어도 괜찮은가?
	@Override
	@GetMapping("/month")
	public ResponseEntity<List<AdateViewResponse>> getAdatesByMonth(
		@AuthenticationPrincipal MemberPrincipal memberPrincipal,
		@RequestParam int year,
		@RequestParam int month
	) {
		Member member = memberPrincipal.getMember();
		final List<AdateDto> adates = adateService.getAdatesByMonth(member, year, month);
		final List<AdateViewResponse> responses = adates.stream()
														.map(AdateMapper::toAdateViewResponse)
														.toList();

		return ResponseEntity.ok(responses);
	}

	@Override
	@GetMapping("/day")
	public ResponseEntity<List<AdateViewResponse>> getAdatesByWeeks(
		@AuthenticationPrincipal MemberPrincipal memberPrincipal,
		@RequestParam LocalDate firstDay,
		@RequestParam LocalDate lastDay
	) {
		Member member = memberPrincipal.getMember();
		final List<AdateDto> adates = adateService.getAdatesByWeek(member, firstDay, lastDay);
		final List<AdateViewResponse> responses = adates.stream()
														.map(AdateMapper::toAdateViewResponse)
														.toList();

		return ResponseEntity.ok(responses);
	}
}
