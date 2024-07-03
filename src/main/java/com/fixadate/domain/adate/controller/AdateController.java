package com.fixadate.domain.adate.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.fixadate.domain.adate.dto.request.AdateRegistRequest;
import com.fixadate.domain.adate.dto.request.AdateUpdateRequest;
import com.fixadate.domain.adate.dto.response.AdateResponse;
import com.fixadate.domain.adate.dto.response.AdateViewResponse;
import com.fixadate.global.jwt.MemberPrincipal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "AdateController", description = "AdateController 입니다.")
public interface AdateController {

	@Operation(summary = "Adate 이벤트 등록", description = "Adate 이벤트를 등록합니다.")
	@Parameter(name = "accessToken", description = "Authorization : Bearer + <jwt>", in = ParameterIn.HEADER)
	@RequestBody(content = @Content(schema = @Schema(implementation = AdateRegistRequest.class)), required = true)
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "created",
			content = @Content(schema = @Schema(implementation = Void.class))),
		@ApiResponse(responseCode = "401", description = "jwt 만료되었을 때 생기는 예외",
			content = @Content(schema = @Schema(implementation = Void.class))),
		@ApiResponse(responseCode = "404", description = "name를 tag에서 찾을 수 없을 때 생기는 예외",
			content = @Content(schema = @Schema(implementation = Void.class)))
	})
	ResponseEntity<AdateRegistRequest> registerAdateEvent(AdateRegistRequest adateRegistRequest,
		MemberPrincipal memberPrincipal);

	@Operation(summary = "Adate 캘린더 이벤트 조회[시간 조회]", description = "지정된 범위에 해당하는 calendar를 조회합니다.[jwt 필요]")
	@Parameters({
		@Parameter(name = "accessToken", description = "Authorization : Bearer + <jwt>", in = ParameterIn.HEADER),
		@Parameter(name = "startDateTime", required = true, description = "00-00-00 ~ 23-59-59",
			content = @Content(schema = @Schema(implementation = LocalDateTime.class))),
		@Parameter(name = "endDateTime", required = true, description = "00-00-00 ~ 23-59-59",
			content = @Content(schema = @Schema(implementation = LocalDateTime.class)))
	})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK",
			content = @Content(schema = @Schema(implementation = AdateResponse.class))),
		@ApiResponse(responseCode = "401", description = "jwt 만료되었을 때 생기는 예외",
			content = @Content(schema = @Schema(implementation = Void.class))),
	})
	ResponseEntity<List<AdateViewResponse>> getAdates(MemberPrincipal memberPrincipal,
		LocalDateTime startDateTime, LocalDateTime endDateTime);

	@Operation(summary = "Adate 캘린더 이벤트 조회[월 조회]", description = "해당 월에 존재하는 calendar를 조회합니다.[jwt 필요]")
	@Parameters({
		@Parameter(name = "accessToken", description = "Authorization : Bearer + <jwt>", in = ParameterIn.HEADER),
		@Parameter(name = "year", required = true, description = "해당 년도를 보내면 됩니다.",
			content = @Content(schema = @Schema(implementation = Integer.class))),
		@Parameter(name = "month", required = true, description = "해당 월를 보내면 됩니다.",
			content = @Content(schema = @Schema(implementation = Integer.class)))
	})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK",
			content = @Content(schema = @Schema(implementation = AdateResponse.class))),
		@ApiResponse(responseCode = "401", description = "jwt 만료되었을 때 생기는 예외",
			content = @Content(schema = @Schema(implementation = Void.class))),
	})
	ResponseEntity<List<AdateViewResponse>> getAdatesByMonth(MemberPrincipal memberPrincipal, int year,
		int month);

	@Operation(summary = "Adate 캘린더 이벤트 조회[일 조회]", description = "해당 기간에 존재하는 calendar를 조회합니다.[jwt 필요]")
	@Parameters({
		@Parameter(name = "accessToken", description = "Authorization : Bearer + <jwt>", in = ParameterIn.HEADER),
		@Parameter(name = "firstDay", required = true, description = "시작 일를 보내면 됩니다.",
			content = @Content(schema = @Schema(implementation = LocalDate.class))),
		@Parameter(name = "lastDay", required = true, description = "마지막 일를 보내면 됩니다.",
			content = @Content(schema = @Schema(implementation = LocalDate.class))),
	})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK",
			content = @Content(schema = @Schema(implementation = AdateResponse.class))),
		@ApiResponse(responseCode = "401", description = "jwt 만료되었을 때 생기는 예외",
			content = @Content(schema = @Schema(implementation = Void.class))),
	})
	public ResponseEntity<List<AdateViewResponse>> getAdatesByWeeks(MemberPrincipal memberPrincipal,
		LocalDate firstDay, LocalDate lastDay);

	@Operation(summary = "adate 삭제", description = "adate를 삭제합니다.[jwt 필요]")
	@Parameters({
		@Parameter(name = "accessToken", description = "Authorization : Bearer + <jwt>", in = ParameterIn.HEADER),
		@Parameter(name = "calendarId", required = true, description = "calendarId / id(pk)와 다름 / calendar를 생성할 때 서버에서 생성하는 10글자의 문자열")
	})
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "No Content",
			content = @Content(schema = @Schema(implementation = Void.class))),
		@ApiResponse(responseCode = "401", description = "jwt 만료되었을 때 생기는 예외",
			content = @Content(schema = @Schema(implementation = Void.class))),
	})
	ResponseEntity<Void> removeAdate(String calendarId);

	@Operation(summary = "Adate 수정", description = "Adate를 수정합니다.")
	@Parameter(name = "accessToken", description = "Authorization : Bearer + <jwt>", in = ParameterIn.HEADER)
	@RequestBody(description = "adate를 수정 내용 / boolean 값인 ifAllDay와 reminders는 값을 반드시 보내야함. 만약 사용자가 입력하지 않으면 이전의 값을 보내야함.",
		content = @Content(schema = @Schema(implementation = AdateUpdateRequest.class)), required = true)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK",
			content = @Content(schema = @Schema(implementation = AdateResponse.class))),
		@ApiResponse(responseCode = "401", description = "jwt 만료되었을 때 생기는 예외",
			content = @Content(schema = @Schema(implementation = Void.class))),
		@ApiResponse(responseCode = "404", description = "name를 tag에서 찾을 수 없을 때 생기는 예외",
			content = @Content(schema = @Schema(implementation = Void.class))),
		@ApiResponse(responseCode = "404", description = "calendarId로 adate를 찾을 수 없을 때 생기는 예외",
			content = @Content(schema = @Schema(implementation = Void.class)))
	})
	public ResponseEntity<AdateResponse> updateAdate(String calendarId,
		AdateUpdateRequest adateUpdateRequest, MemberPrincipal memberPrincipal);
}
