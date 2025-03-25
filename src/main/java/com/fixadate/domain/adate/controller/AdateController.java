package com.fixadate.domain.adate.controller;

import com.fixadate.domain.adate.dto.request.ToDoStatusUpdateRequest;
import com.fixadate.domain.adate.dto.request.TodoRegisterRequest;
import com.fixadate.domain.adate.dto.response.AdateInfoResponse;
import com.fixadate.global.dto.GeneralResponseDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.fixadate.domain.adate.dto.request.AdateRegisterRequest;
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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Tag(name = "AdateController", description = "AdateController 입니다.")
public interface AdateController {

	@Operation(summary = "Adate 이벤트 등록", description = "Adate 이벤트를 등록합니다.")
	@Parameter(name = "accessToken", description = "Authorization : Bearer + <jwt>", in = ParameterIn.HEADER)
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "created",
			content = @Content(schema = @Schema(implementation = Void.class))),
		@ApiResponse(responseCode = "401", description = "jwt 만료되었을 때 생기는 예외",
			content = @Content(schema = @Schema(implementation = Void.class))),
		@ApiResponse(responseCode = "404", description = "name를 tag에서 찾을 수 없을 때 생기는 예외",
			content = @Content(schema = @Schema(implementation = Void.class)))
	})
	GeneralResponseDto registerAdate(
		@Valid @RequestBody final AdateRegisterRequest adateRegisterRequest,
		@AuthenticationPrincipal final MemberPrincipal memberPrincipal
	);

	@Operation(summary = "Adate 복원", description = "삭제된 Adate를 복원합니다.")
	GeneralResponseDto restoreAdate(@PathVariable final String calendarId);

	@Operation(summary = "Adate 조회", description = "특정 Adate를 조회합니다.")
	GeneralResponseDto getAdate(@PathVariable final String calendarId);

	@Operation(summary = "Adate 캘린더 이벤트 조회[월 조회]", description = "해당 월에 존재하는 calendar를 조회합니다.[jwt 필요]")
	@Parameters({
		@Parameter(name = "accessToken", description = "Authorization : Bearer + <jwt>", in = ParameterIn.HEADER),
		@Parameter(name = "year", required = true, description = "해당 년도를 보내면 됩니다."),
		@Parameter(name = "month", required = true, description = "해당 월을 보내면 됩니다.")
	})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK",
			content = @Content(schema = @Schema(implementation = AdateInfoResponse.class))),
		@ApiResponse(responseCode = "401", description = "jwt 만료되었을 때 생기는 예외",
			content = @Content(schema = @Schema(implementation = Void.class))),
	})
	GeneralResponseDto getAdatesBy(
		@AuthenticationPrincipal final MemberPrincipal memberPrincipal,
		@RequestParam final int year,
		@RequestParam @Min(1) @Max(12) final int month
	);

//	@Operation(summary = "Adate 캘린더 이벤트 조회[시간 조회]", description = "지정된 범위에 해당하는 calendar를 조회합니다.[jwt 필요]")
//	@Parameters({
//		@Parameter(name = "accessToken", description = "Authorization : Bearer + <jwt>", in = ParameterIn.HEADER),
//		@Parameter(name = "startDateTime", required = true, description = "00-00-00 ~ 23-59-59"),
//		@Parameter(name = "endDateTime", required = true, description = "00-00-00 ~ 23-59-59")
//	})
//	@ApiResponses({
//		@ApiResponse(responseCode = "200", description = "OK",
//			content = @Content(schema = @Schema(implementation = AdateResponse.class))),
//		@ApiResponse(responseCode = "401", description = "jwt 만료되었을 때 생기는 예외",
//			content = @Content(schema = @Schema(implementation = Void.class))),
//	})
//	GeneralResponseDto getAdatesBy(
//		@AuthenticationPrincipal final MemberPrincipal memberPrincipal,
//		@RequestParam final LocalDateTime startDateTime,
//		@RequestParam final LocalDateTime endDateTime
//	);

//	@Operation(summary = "Adate 캘린더 이벤트 조회[일 조회]", description = "해당 기간에 존재하는 calendar를 조회합니다.[jwt 필요]")
//	@Parameters({
//		@Parameter(name = "accessToken", description = "Authorization : Bearer + <jwt>", in = ParameterIn.HEADER),
//		@Parameter(name = "firstDay", required = true, description = "시작 일을 보내면 됩니다."),
//		@Parameter(name = "lastDay", required = true, description = "마지막 일을 보내면 됩니다.")
//	})
//	@ApiResponses({
//		@ApiResponse(responseCode = "200", description = "OK",
//			content = @Content(schema = @Schema(implementation = AdateResponse.class))),
//		@ApiResponse(responseCode = "401", description = "jwt 만료되었을 때 생기는 예외",
//			content = @Content(schema = @Schema(implementation = Void.class))),
//	})
//	GeneralResponseDto getAdatesBy(
//		@AuthenticationPrincipal final MemberPrincipal memberPrincipal,
//		@RequestParam final LocalDate firstDay,
//		@RequestParam final LocalDate lastDay
//	);

	@Operation(summary = "Adate 수정", description = "Adate를 수정합니다.")
	@Parameter(name = "accessToken", description = "Authorization : Bearer + <jwt>", in = ParameterIn.HEADER)
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
	GeneralResponseDto updateAdate(
		@PathVariable final String calendarId,
		@Valid @RequestBody final AdateUpdateRequest adateUpdateRequest,
		@AuthenticationPrincipal final MemberPrincipal memberPrincipal
	);

	@Operation(summary = "adate 삭제", description = "adate를 삭제합니다.[jwt 필요]")
	@Parameters({
		@Parameter(name = "accessToken", description = "Authorization : Bearer + <jwt>", in = ParameterIn.HEADER),
		@Parameter(name = "calendarId", required = true,
			description = "calendarId / id(pk)와 다름 / calendar를 생성할 때 서버에서 생성하는 10글자의 문자열")
	})
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "No Content",
			content = @Content(schema = @Schema(implementation = Void.class))),
		@ApiResponse(responseCode = "401", description = "jwt 만료되었을 때 생기는 예외",
			content = @Content(schema = @Schema(implementation = Void.class))),
	})
	GeneralResponseDto removeAdate(@PathVariable final String calendarId);

	@Operation(summary = "ToDo 등록", description = "ToDo를 등록합니다.")
	@Parameter(name = "accessToken", description = "Authorization : Bearer + <jwt>", in = ParameterIn.HEADER)
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "created",
					content = @Content(schema = @Schema(implementation = Void.class))),
			@ApiResponse(responseCode = "200", description = "jwt 만료되었을 때 생기는 예외", //401
					content = @Content(schema = @Schema(implementation = Void.class))),
			@ApiResponse(responseCode = "200", description = "name를 tag에서 찾을 수 없을 때 생기는 예외", //404
					content = @Content(schema = @Schema(implementation = Void.class)))
	})
	GeneralResponseDto registerToDo(
			@Valid @RequestBody final TodoRegisterRequest todoRegisterRequest,
			@AuthenticationPrincipal final MemberPrincipal memberPrincipal
	);

	@Operation(summary = "ToDoStatus 변경", description = "ToDoStatus를 변경합니다.")
	@Parameter(name = "accessToken", description = "Authorization : Bearer + <jwt>", in = ParameterIn.HEADER)
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "created",
					content = @Content(schema = @Schema(implementation = Void.class))),
			@ApiResponse(responseCode = "200", description = "jwt 만료되었을 때 생기는 예외", //401
					content = @Content(schema = @Schema(implementation = Void.class))),
			@ApiResponse(responseCode = "200", description = "name를 tag에서 찾을 수 없을 때 생기는 예외", //404
					content = @Content(schema = @Schema(implementation = Void.class)))
	})
	GeneralResponseDto updateToDoStatus(
			@Valid @RequestBody final ToDoStatusUpdateRequest toDoStatusUpdateRequest,
			@AuthenticationPrincipal final MemberPrincipal memberPrincipal
	);

	@Operation(summary = "ToDo 삭제", description = "ToDo를 삭제합니다.")
	@Parameter(name = "accessToken", description = "Authorization : Bearer + <jwt>", in = ParameterIn.HEADER)
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "created",
					content = @Content(schema = @Schema(implementation = Void.class))),
			@ApiResponse(responseCode = "200", description = "jwt 만료되었을 때 생기는 예외", //401
					content = @Content(schema = @Schema(implementation = Void.class))),
			@ApiResponse(responseCode = "200", description = "name를 tag에서 찾을 수 없을 때 생기는 예외", //404
					content = @Content(schema = @Schema(implementation = Void.class)))
	})
	GeneralResponseDto deleteToDo(
			@PathVariable final String toDoId,
			@AuthenticationPrincipal final MemberPrincipal memberPrincipal
	);
}
