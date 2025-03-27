package com.fixadate.domain.dates.controller;


import com.fixadate.domain.dates.dto.*;
import com.fixadate.domain.dates.dto.request.DatesCoordinationRegisterRequest;
import com.fixadate.domain.dates.dto.request.DatesUpdateRequest;
import com.fixadate.domain.dates.dto.response.DatesCoordinationResponse;
import com.fixadate.domain.dates.dto.response.DatesDetailResponse;
import com.fixadate.domain.dates.dto.response.DatesInfoResponse;
import com.fixadate.domain.dates.dto.response.DatesResponse;
import com.fixadate.domain.dates.mapper.DatesMapper;
import com.fixadate.domain.dates.service.DatesService;
import com.fixadate.domain.main.dto.DatesMemberInfo;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.dto.GeneralResponseDto;
import com.fixadate.global.jwt.MemberPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "DatesController", description = "DatesController 입니다.")
@RestController
@RequestMapping("/v1/teams")
public class DatesController {

    private final DatesService datesService;

    @Autowired
    public DatesController(DatesService datesService) {
        this.datesService = datesService;
    }

    @Operation(summary = "Dates 생성(=일정취합)", description = "일정취합을 생성합니다.")
    @Parameter(name = "accessToken", description = "Authorization : Bearer + <jwt>", in = ParameterIn.HEADER)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "success",
            content = @Content(schema = @Schema(implementation = DatesCoordinationResponse.class))),
        @ApiResponse(responseCode = "401", description = "jwt 만료되었을 때 생기는 예외",
            content = @Content(schema = @Schema(implementation = Void.class))),
        @ApiResponse(responseCode = "4000", description = "투표 종료일이 현재 시간과 같거나 이전인 경우",
            content = @Content(schema = @Schema(implementation = Void.class))),
    })
    @PostMapping("/dates")
    public GeneralResponseDto createDatesCoordination(@Valid @RequestBody final DatesCoordinationRegisterRequest request,
        @AuthenticationPrincipal final MemberPrincipal memberPrincipal){
        final Member member = memberPrincipal.getMember();
        final DatesCoordinationRegisterDto datesCoordinationRegisterDto = DatesMapper.toDatesCoordinationRegisterDto(request);

        // 투표 종료일에 대한 유효성 검사는 필요
        LocalDateTime now = LocalDateTime.now();
        if(datesCoordinationRegisterDto.endsWhen().isBefore(now)){
            return GeneralResponseDto.create("4000", "투표 종료일은 현재 시간 이후여야 합니다.", null);
        }

        DatesCoordinationDto datesCoordinationDto = datesService.createDatesCoordination(datesCoordinationRegisterDto, member);
        DatesCoordinationResponse datesCoordinationResponse = DatesMapper.toDatesCoordinationResponse(datesCoordinationDto);
        return GeneralResponseDto.success("", datesCoordinationResponse);
    }

    @PatchMapping("/dates/{id}")
    public GeneralResponseDto updateDates(
        @AuthenticationPrincipal final MemberPrincipal memberPrincipal,
        @PathVariable Long id,
        @Valid @RequestBody final DatesUpdateRequest request
    ){
        final Member member = memberPrincipal.getMember();
        final DatesUpdateDto datesUpdateDto = DatesMapper.toDatesUpdateDto(request);
        DatesDto datesDto = datesService.updateDates(datesUpdateDto, id, member);
        List<DatesMemberInfo> datesMemberList = new ArrayList<>();
        DatesResponse datesResponse = DatesMapper.toDatesResponse(datesDto, datesMemberList);

        return GeneralResponseDto.success("", datesResponse);
    }

    @DeleteMapping("/dates/{id}")
    public GeneralResponseDto deleteDates(
        @AuthenticationPrincipal final MemberPrincipal memberPrincipal,
        @PathVariable Long id){
        final Member member = memberPrincipal.getMember();
        boolean result = datesService.deleteDates(id, member);
        return GeneralResponseDto.success("", result);
    }

    @GetMapping("/dates/{id}")
    public GeneralResponseDto getDatesDetail(
        @AuthenticationPrincipal final MemberPrincipal memberPrincipal,
        @PathVariable Long id){
        final Member member = memberPrincipal.getMember();
        DatesDetailResponse result = datesService.getDatesDetail(id, member);
        return GeneralResponseDto.success("", result);
    }

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
    @GetMapping("/month")
    public GeneralResponseDto getDatesBy(
        @AuthenticationPrincipal final MemberPrincipal memberPrincipal,
        @RequestParam final int year,
        @RequestParam @Min(1) @Max(12) final int month
    ) {
        final Member member = memberPrincipal.getMember();
        final DatesInfoResponse response = datesService.getDatesByMonth(member, year, month);
        return GeneralResponseDto.success("", response);
    }
}

