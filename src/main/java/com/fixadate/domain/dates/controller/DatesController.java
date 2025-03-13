package com.fixadate.domain.dates.controller;


import com.fixadate.domain.dates.dto.*;
import com.fixadate.domain.dates.dto.request.DatesRegisterRequest;
import com.fixadate.domain.dates.dto.request.DatesUpdateRequest;
import com.fixadate.domain.dates.dto.response.DatesDetailResponse;
import com.fixadate.domain.dates.dto.response.DatesResponse;
import com.fixadate.domain.dates.mapper.DatesMapper;
import com.fixadate.domain.dates.service.DatesService;
import com.fixadate.domain.main.dto.DatesMemberInfo;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.dto.GeneralResponseDto;
import com.fixadate.global.jwt.MemberPrincipal;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teams")
public class DatesController {

    private final DatesService datesService;

    @Autowired
    public DatesController(DatesService datesService) {
        this.datesService = datesService;
    }

    @PostMapping("/dates")
    public GeneralResponseDto createDates(@Valid @RequestBody final DatesRegisterRequest request,
        @AuthenticationPrincipal final MemberPrincipal memberPrincipal){

        final Member member = memberPrincipal.getMember();
        final DatesRegisterDto datesRegisterDto = DatesMapper.toDatesRegisterDto(request);
        DatesDto datesDto = datesService.createDates(datesRegisterDto, member);
        DatesResponse datesResponse = DatesMapper.toDatesResponse(datesDto, new ArrayList<>());
        return GeneralResponseDto.success("", datesResponse);
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
}

