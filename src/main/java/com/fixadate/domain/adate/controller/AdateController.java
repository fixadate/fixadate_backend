package com.fixadate.domain.adate.controller;

import com.fixadate.domain.adate.dto.request.AdateRegistRequest;
import com.fixadate.domain.adate.dto.response.AdateCalendarEventResponse;
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
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
            @ApiResponse(responseCode = "404", description = "color를 colorType에서 찾을 수 없을 때 생기는 예외",
                    content = @Content(schema = @Schema(implementation = Void.class)))
    })
    ResponseEntity<Void> registerAdateEvent(AdateRegistRequest adateRegistRequest, MemberPrincipal memberPrincipal);

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
                    content = @Content(schema = @Schema(implementation = AdateCalendarEventResponse.class))),
            @ApiResponse(responseCode = "401", description = "jwt 만료되었을 때 생기는 예외",
                    content = @Content(schema = @Schema(implementation = Void.class))),
    })
    ResponseEntity<List<AdateCalendarEventResponse>> getAdates(MemberPrincipal memberPrincipal, LocalDateTime startDateTime, LocalDateTime endDateTime);

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
                    content = @Content(schema = @Schema(implementation = AdateCalendarEventResponse.class))),
            @ApiResponse(responseCode = "401", description = "jwt 만료되었을 때 생기는 예외",
                    content = @Content(schema = @Schema(implementation = Void.class))),
    })
    ResponseEntity<List<AdateCalendarEventResponse>> getAdatesByMonth(MemberPrincipal memberPrincipal, int year, int month);

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
                    content = @Content(schema = @Schema(implementation = AdateCalendarEventResponse.class))),
            @ApiResponse(responseCode = "401", description = "jwt 만료되었을 때 생기는 예외",
                    content = @Content(schema = @Schema(implementation = Void.class))),
    })
    public ResponseEntity<List<AdateCalendarEventResponse>> getAdatesByWeeks(MemberPrincipal memberPrincipal, LocalDate firstDay, LocalDate lastDay);

    @Operation(summary = "멤버별 Adate 조회", description = "멤버별 Adate를 조회합니다.[jwt 필요]")
    @Parameters({
            @Parameter(name = "accessToken", description = "Authorization : Bearer + <jwt>", in = ParameterIn.HEADER),
            @Parameter(name = "name", required = true, description = "member의 이름")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = AdateCalendarEventResponse.class))),
            @ApiResponse(responseCode = "401", description = "jwt 만료되었을 때 생기는 예외",
                    content = @Content(schema = @Schema(implementation = Void.class))),
    })
    ResponseEntity<List<AdateCalendarEventResponse>> getAdatesByMemberName(String memberName);
}
