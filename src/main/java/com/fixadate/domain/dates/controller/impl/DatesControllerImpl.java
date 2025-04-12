package com.fixadate.domain.dates.controller.impl;

import com.fixadate.domain.dates.controller.DatesController;
import com.fixadate.domain.dates.dto.*;
import com.fixadate.domain.dates.dto.request.ChoiceDatesRequest;
import com.fixadate.domain.dates.dto.request.DatesCoordinationRegisterRequest;
import com.fixadate.domain.dates.dto.request.DatesUpdateRequest;
import com.fixadate.domain.dates.dto.response.*;
import com.fixadate.domain.dates.entity.Dates;
import com.fixadate.domain.dates.entity.DatesCoordinations;
import com.fixadate.domain.dates.mapper.DatesMapper;
import com.fixadate.domain.dates.service.DatesService;
import com.fixadate.domain.main.dto.DatesMemberInfo;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.annotation.RestControllerWithMapping;
import com.fixadate.global.dto.GeneralResponseDto;
import com.fixadate.global.jwt.MemberPrincipal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestControllerWithMapping("/v1/dates")
public class DatesControllerImpl implements DatesController {

    private final DatesService datesService;

    @Autowired
    public DatesControllerImpl(DatesService datesService) {
        this.datesService = datesService;
    }

    @Override
    @GetMapping("/team/{teamId}")
    public GeneralResponseDto getInvitableMemberByTeam(
        @AuthenticationPrincipal final MemberPrincipal memberPrincipal,
        @PathVariable Long teamId
    ) {
        final Member member = memberPrincipal.getMember();
        final List<TeamListResponse.TeamMemberList> response = datesService.getInvitableMemberByTeam(member, teamId);
        return GeneralResponseDto.success("", response);
    }

    @Override
    @PostMapping
    public GeneralResponseDto createDatesCoordination(@Valid @RequestBody final DatesCoordinationRegisterRequest request,
        @AuthenticationPrincipal final MemberPrincipal memberPrincipal){
        final Member member = memberPrincipal.getMember();
        final DatesCoordinationRegisterDto datesCoordinationRegisterDto = DatesMapper.toDatesCoordinationRegisterDto(request);

        // 투표 종료일에 대한 유효성 검사는 필요
        LocalDateTime now = LocalDateTime.now();
        if(datesCoordinationRegisterDto.startsWhen().isBefore(now)){
            return GeneralResponseDto.fail("4000", "투표 시작일은 현재 시간 이후여야 합니다.", null);
        }
        if(datesCoordinationRegisterDto.endsWhen().isBefore(now)){
            return GeneralResponseDto.fail("4001", "투표 종료일은 현재 시간 이후여야 합니다.", null);
        }

        DatesCoordinationDto datesCoordinationDto = datesService.createDatesCoordination(datesCoordinationRegisterDto, member);
        DatesCoordinationResponse datesCoordinationResponse = DatesMapper.toDatesCoordinationResponse(datesCoordinationDto);
        return GeneralResponseDto.success("", datesCoordinationResponse);
    }

    @Override
    @PatchMapping("/{id}")
    public GeneralResponseDto updateDates(
        @AuthenticationPrincipal final MemberPrincipal memberPrincipal,
        @PathVariable Long id,
        @Valid @RequestBody final DatesUpdateRequest request
    ){
        final Member member = memberPrincipal.getMember();
        final DatesUpdateDto datesUpdateDto = DatesMapper.toDatesUpdateDto(request);
        DatesDto datesDto = datesService.updateDates(datesUpdateDto, id, member);
        List<DatesMemberInfo> datesMemberList = new ArrayList<>();
        DatesResponse datesResponse = DatesMapper.toDatesResponse(member, datesDto, datesMemberList);

        return GeneralResponseDto.success("", datesResponse);
    }

    @Override
    @DeleteMapping("/{id}")
    public GeneralResponseDto deleteDates(
        @AuthenticationPrincipal final MemberPrincipal memberPrincipal,
        @PathVariable Long id){
        final Member member = memberPrincipal.getMember();
        boolean result = datesService.deleteDates(id, member);
        return GeneralResponseDto.success("", result);
    }

    @Override
    @GetMapping("/{id}")
    public GeneralResponseDto getDatesDetail(
        @AuthenticationPrincipal final MemberPrincipal memberPrincipal,
        @PathVariable Long id){
        final Member member = memberPrincipal.getMember();
        final Dates dates = datesService.getDates(id);
        DatesDetailResponse result = datesService.getDatesDetail(dates, member);
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

    @Override
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

    @Override
    @GetMapping("/datesCoordination/{id}")
    public GeneralResponseDto getDatesCollections(
        @AuthenticationPrincipal final MemberPrincipal memberPrincipal,
        @RequestParam final Long id
    ) {
        final Member member = memberPrincipal.getMember();
        final DatesCoordinations datesCoordinations = datesService.getDatesCoordination(member, id);
        if(datesCoordinations == null){
            return GeneralResponseDto.fail("4000", "해당 일정취합이 존재하지 않습니다.", null);
        }

        DatesCoordinations.CollectStatus collectStatus = datesCoordinations.getCollectStatus();
        switch (collectStatus) {
            // todo: 상태 처리
////            case COLLECTING -> datesCoordinations.setCollectStatus(DatesCoordinations.CollectStatus.COLLECTING);
////            case CHOOSING -> datesCoordinations.setCollectStatus(DatesCoordinations.CollectStatus.COLLECTED);
//            case CONFIRMED -> new GeneralResponseDto.fail("4013", "해당 일정은 확정되었습니다.", null);
//            case CANCELLED -> datesCoordinations.setCollectStatus(DatesCoordinations.CollectStatus.CONFIRMED);
//            default -> {
//            }
        }


        final DatesCollectionsResponse response = datesService.getDatesCollections(member, datesCoordinations);
        return GeneralResponseDto.success("", response);
    }

    @Override
    @PostMapping("/datesCoordination/{id}")
    public GeneralResponseDto choiceDates(
        @AuthenticationPrincipal final MemberPrincipal memberPrincipal,
        @RequestParam final Long id,
        @RequestParam final ChoiceDatesRequest choiceDatesRequest
    ) {
        final Member member = memberPrincipal.getMember();
        final DatesCoordinations datesCoordinations = datesService.getDatesCoordination(member, id);
        if(datesCoordinations == null){
            return GeneralResponseDto.fail("4000", "해당 일정취합이 존재하지 않습니다.", null);
        }
        return datesService.choiceDates(member, datesCoordinations, choiceDatesRequest);
    }

    @Override
    @GetMapping("/datesCoordination/{id}/confirm")
    public GeneralResponseDto getDatesConfirmData(
        @AuthenticationPrincipal final MemberPrincipal memberPrincipal,
        @RequestParam final Long id
    ) {
        final Member member = memberPrincipal.getMember();
        final DatesCoordinations datesCoordinations = datesService.getDatesCoordination(member, id);
        if(datesCoordinations == null){
            return GeneralResponseDto.fail("4000", "해당 일정취합이 존재하지 않습니다.", null);
        }
        final GetDatesConfirmResponse response = datesService.getDatesConfirm(member, datesCoordinations);
        return GeneralResponseDto.success("", response);
    }

    @Override
    @PostMapping("/datesCoordination/{id}/confirm")
    public GeneralResponseDto confirmDatesCoordinations(
        @AuthenticationPrincipal final MemberPrincipal memberPrincipal,
        @RequestParam final Long id
    ) {
        final Member member = memberPrincipal.getMember();
        final DatesCoordinations datesCoordinations = datesService.getDatesCoordination(member, id);
        if(datesCoordinations == null){
            return GeneralResponseDto.fail("4000", "해당 일정취합이 존재하지 않습니다.", null);
        }
        final boolean response = datesService.confirmDatesCoordinations(member, datesCoordinations);
        return GeneralResponseDto.success("", response);
    }

    @Override
    @DeleteMapping("/datesCoordination/{id}/confirm")
    public GeneralResponseDto cancelDatesCoordinations(
        @AuthenticationPrincipal final MemberPrincipal memberPrincipal,
        @RequestParam final Long id
    ) {
        final Member member = memberPrincipal.getMember();
        final DatesCoordinations datesCoordinations = datesService.getDatesCoordination(member, id);
        if(datesCoordinations == null){
            return GeneralResponseDto.fail("4000", "해당 일정취합이 존재하지 않습니다.", null);
        }
        final boolean response = datesService.cancelDatesCoordinations(member, datesCoordinations);
        return GeneralResponseDto.success("", response);
    }
}

