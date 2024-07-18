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
import com.fixadate.domain.adate.dto.request.AdateRegisterRequest;
import com.fixadate.domain.adate.dto.request.AdateUpdateRequest;
import com.fixadate.domain.adate.dto.response.AdateResponse;
import com.fixadate.domain.adate.dto.response.AdateViewResponse;
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
		@AuthenticationPrincipal MemberPrincipal memberPrincipal) {
		Member member = memberPrincipal.getMember();
		adateService.registerAdateEvent(adateRegisterRequest, adateRegisterRequest.tagName(), member);
		return ResponseEntity.ok(adateRegisterRequest);
	}

	@Override
	@GetMapping()
	public ResponseEntity<List<AdateViewResponse>> getAdates(
		@AuthenticationPrincipal MemberPrincipal memberPrincipal,
		@RequestParam LocalDateTime startDateTime,
		@RequestParam LocalDateTime endDateTime) {
		Member member = memberPrincipal.getMember();
		List<AdateViewResponse> adateRespons = adateService
			.getAdateByStartAndEndTime(member, startDateTime, endDateTime);
		return ResponseEntity.ok(adateRespons);
	}

	@PostMapping("/restore/{calendarId}")
	public ResponseEntity<AdateResponse> restoreAdate(@PathVariable String calendarId) {
		AdateResponse adateResponse = adateService.restoreAdateByCalendarId(calendarId);
		return ResponseEntity.ok(adateResponse);
	}

	@GetMapping("/{calendarId}")
	public ResponseEntity<AdateResponse> getAdate(@PathVariable String calendarId) {
		AdateResponse adateResponse = adateService.getAdateResponseByCalendarId(calendarId);
		return ResponseEntity.ok(adateResponse);
	}

	@PatchMapping("/{calendarId}")
	public ResponseEntity<AdateResponse> updateAdate(@PathVariable String calendarId,
		@Valid @RequestBody AdateUpdateRequest adateUpdateRequest,
		@AuthenticationPrincipal MemberPrincipal memberPrincipal) {
		AdateResponse adateResponse = adateService.updateAdate(calendarId, adateUpdateRequest,
			memberPrincipal.getMember());
		return ResponseEntity.ok(adateResponse);
	}

	@Override
	@DeleteMapping("/{calendarId}")
	public ResponseEntity<Void> removeAdate(@PathVariable String calendarId) {
		adateService.removeAdateByCalendarId(calendarId);
		return ResponseEntity.noContent().build();
	}

	@Override
	@GetMapping("/month")
	public ResponseEntity<List<AdateViewResponse>> getAdatesByMonth(
		@AuthenticationPrincipal MemberPrincipal memberPrincipal,
		@RequestParam int year,
		@RequestParam int month) {
		Member member = memberPrincipal.getMember();
		List<AdateViewResponse> adateRespons = adateService
			.getAdatesByMonth(year, month, member);
		return ResponseEntity.ok(adateRespons);
	}

	@Override
	@GetMapping("/day")
	public ResponseEntity<List<AdateViewResponse>> getAdatesByWeeks(
		@AuthenticationPrincipal MemberPrincipal memberPrincipal,
		@RequestParam LocalDate firstDay,
		@RequestParam LocalDate lastDay) {
		Member member = memberPrincipal.getMember();
		List<AdateViewResponse> adateRespons = adateService
			.getAdatesByWeek(firstDay, lastDay, member);
		return ResponseEntity.ok(adateRespons);
	}
}
