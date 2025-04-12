package com.fixadate.domain.dates.controller;

import com.fixadate.domain.dates.dto.request.ChoiceDatesRequest;
import com.fixadate.domain.dates.dto.request.DatesCoordinationRegisterRequest;
import com.fixadate.domain.dates.dto.request.DatesUpdateRequest;
import com.fixadate.domain.dates.dto.response.*;
import com.fixadate.global.dto.GeneralResponseDto;
import com.fixadate.global.jwt.MemberPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "DatesController", description = "DatesController 입니다.")
public interface DatesController {
    @Operation(summary = "팀별 참여가능한 사람 조회", description = "Dates 생성시, 참여가능한 사람 조회")
    @Parameter(name = "accessToken", description = "Authorization : Bearer + <jwt>", in = ParameterIn.HEADER)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TeamListResponse.TeamMemberList.class)))),
            @ApiResponse(responseCode = "401", description = "jwt 만료되었을 때 생기는 예외",
                    content = @Content(schema = @Schema(implementation = Void.class))),
    })
    public GeneralResponseDto getInvitableMemberByTeam(
            @AuthenticationPrincipal final MemberPrincipal memberPrincipal,
            @PathVariable Long teamId
    );

    @Operation(summary = "Dates 생성(=일정취합)", description = "일정취합을 생성합니다.")
    @Parameter(name = "accessToken", description = "Authorization : Bearer + <jwt>", in = ParameterIn.HEADER)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success",
                    content = @Content(schema = @Schema(implementation = DatesCoordinationResponse.class))),
            @ApiResponse(responseCode = "401", description = "jwt 만료되었을 때 생기는 예외",
                    content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "4000", description = "투표 시작일이 현재보다 이전인 경우",
                    content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "4001", description = "투표 종료일이 현재 시간과 같거나 이전인 경우",
                    content = @Content(schema = @Schema(implementation = Void.class))),
    })
    public GeneralResponseDto createDatesCoordination(@Valid @RequestBody final DatesCoordinationRegisterRequest request,
                                                      @AuthenticationPrincipal final MemberPrincipal memberPrincipal);

    public GeneralResponseDto updateDates(
            @AuthenticationPrincipal final MemberPrincipal memberPrincipal,
            @PathVariable Long id,
            @Valid @RequestBody final DatesUpdateRequest request
    );

    public GeneralResponseDto deleteDates(
            @AuthenticationPrincipal final MemberPrincipal memberPrincipal,
            @PathVariable Long id);

    @Operation(summary = "Dates 상세 조회", description = "알람 클릭 시, 해당 일정 상세 조회")
    @Parameter(name = "accessToken", description = "Authorization : Bearer + <jwt>", in = ParameterIn.HEADER)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success",
                    content = @Content(schema = @Schema(implementation = DatesCoordinationResponse.class))),
            @ApiResponse(responseCode = "401", description = "jwt 만료되었을 때 생기는 예외",
                    content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "4004", description = "해당 일정이 존재하지않는 경우",
                    content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "4008", description = "일정 참여자가 아닌 경우",
                    content = @Content(schema = @Schema(implementation = Void.class))),
    })
    public GeneralResponseDto getDatesDetail(
            @AuthenticationPrincipal final MemberPrincipal memberPrincipal,
            @PathVariable Long id);

//    @Operation(summary = "Dates 메인 조회(주별)", description = "Dates 메인 조회를 등록합니다.")
//    @Parameter(name = "accessToken", description = "Authorization : Bearer + <jwt>", in = ParameterIn.HEADER)
//    @ApiResponses({
//        @ApiResponse(responseCode = "200", description = "success",
//            content = @Content(schema = @Schema(implementation = Void.class))),
//        @ApiResponse(responseCode = "401", description = "jwt 만료되었을 때 생기는 예외",
//            content = @Content(schema = @Schema(implementation = Void.class))),
//    })
//    @GetMapping("/week")
//    public GeneralResponseDto getDatesBy(
//        @AuthenticationPrincipal final MemberPrincipal memberPrincipal,
//        @RequestParam final int year,
//        @RequestParam @Min(1) @Max(12) final int month,
//        @RequestParam @Min(1) @Max(6) final int weekNum
//    ) {
//        final Member member = memberPrincipal.getMember();
//        final DatesInfoResponse response = datesService.getDatesByWeek(member, year, month, weekNum);
//        return GeneralResponseDto.success("", "");
//    }

    @Operation(summary = "Dates 메인 조회(월별)", description = "Dates 메인 조회를 등록합니다.")
    @Parameter(name = "accessToken", description = "Authorization : Bearer + <jwt>", in = ParameterIn.HEADER)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success",
                    content = @Content(schema = @Schema(implementation = DatesInfoResponse.class))),
            @ApiResponse(responseCode = "401", description = "jwt 만료되었을 때 생기는 예외",
                    content = @Content(schema = @Schema(implementation = Void.class))),
    })
    public GeneralResponseDto getDatesBy(
            @AuthenticationPrincipal final MemberPrincipal memberPrincipal,
            @RequestParam final int year,
            @RequestParam @Min(1) @Max(12) final int month
    );

    @Operation(summary = "일정취합 조회", description = "일정취합 페이지에 필요한 data입니다.")
    @Parameter(name = "accessToken", description = "Authorization : Bearer + <jwt>", in = ParameterIn.HEADER)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success",
                    content = @Content(schema = @Schema(implementation = DatesCollectionsResponse.class))),
            @ApiResponse(responseCode = "401", description = "jwt 만료되었을 때 생기는 예외",
                    content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "4000", description = "해당 일정취합이 존재하지 않는 경우",
                    content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "4013", description = "해당 일정이 이미 결정된 경우",
                    content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "4014", description = "해당 일정이 취소된 경우",
                    content = @Content(schema = @Schema(implementation = Void.class))),
    })
    public GeneralResponseDto getDatesCollections(
            @AuthenticationPrincipal final MemberPrincipal memberPrincipal,
            @RequestParam final Long id
    );

    @Operation(summary = "일정 선택", description = "원하는 일정을 선택합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success",
                    content = @Content(schema = @Schema(implementation = DatesCollectionsResponse.class))),
            @ApiResponse(responseCode = "401", description = "jwt 만료되었을 때 생기는 예외",
                    content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "4000", description = "해당 일정취합이 존재하지 않는 경우",
                    content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "4001", description = "제안자가 존재하지않는 경새",
                    content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "4007", description = "해당 일정취합 대상자가 아닌 경우",
                    content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "4011", description = "해당 일정에 adate 일정이 존재하는 경우",
                    content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "4012", description = "해당 일정에 dates 일정이 존재하는 경우",
                    content = @Content(schema = @Schema(implementation = Void.class))),
    })
    public GeneralResponseDto choiceDates(
            @AuthenticationPrincipal final MemberPrincipal memberPrincipal,
            @RequestParam final Long id,
            @RequestParam final ChoiceDatesRequest choiceDatesRequest
    );

    @Operation(summary = "일정 확정 조회", description = "일정 확정 페이지에 필요한 데이터입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success",
                    content = @Content(schema = @Schema(implementation = GetDatesConfirmResponse.class))),
            @ApiResponse(responseCode = "401", description = "jwt 만료되었을 때 생기는 예외",
                    content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "4000", description = "해당 일정취합이 존재하지 않는 경우",
                    content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "4009", description = "일정 제안자가 아닌 경우",
                    content = @Content(schema = @Schema(implementation = Void.class))),
    })
    public GeneralResponseDto getDatesConfirmData(
            @AuthenticationPrincipal final MemberPrincipal memberPrincipal,
            @RequestParam final Long id
    );

    @Operation(summary = "일정 확정", description = "일정 확정을 합니다.")
    public GeneralResponseDto confirmDatesCoordinations(
            @AuthenticationPrincipal final MemberPrincipal memberPrincipal,
            @RequestParam final Long id
    );

    @Operation(summary = "일정 취소", description = "일정 취소를 합니다.")
    public GeneralResponseDto cancelDatesCoordinations(
            @AuthenticationPrincipal final MemberPrincipal memberPrincipal,
            @RequestParam final Long id
    );
}
