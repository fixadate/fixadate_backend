package com.fixadate.domain.dates.controller;


import com.fixadate.domain.dates.dto.*;
import com.fixadate.domain.dates.dto.request.DatesRegisterRequest;
import com.fixadate.domain.dates.dto.request.DatesUpdateRequest;
import com.fixadate.domain.dates.dto.response.DatesResponse;
import com.fixadate.domain.dates.mapper.DatesMapper;
import com.fixadate.domain.dates.service.DatesService;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.jwt.MemberPrincipal;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<DatesResponse> createDates(@Valid @RequestBody final DatesRegisterRequest request,
        @AuthenticationPrincipal final MemberPrincipal memberPrincipal){

        final Member member = memberPrincipal.getMember();
        final DatesRegisterDto datesRegisterDto = DatesMapper.toDatesRegisterDto(request);
        DatesDto datesDto = datesService.createDates(datesRegisterDto, member);
        DatesResponse datesResponse = DatesMapper.toDatesResponse(datesDto);
        return ResponseEntity.ok(datesResponse);
    }

    @PatchMapping("/dates/{id}")
    public ResponseEntity<DatesResponse> updateDates(
        @AuthenticationPrincipal final MemberPrincipal memberPrincipal,
        @PathVariable Long id,
        @Valid @RequestBody final DatesUpdateRequest request
    ){
        final Member member = memberPrincipal.getMember();
        final DatesUpdateDto datesUpdateDto = DatesMapper.toDatesUpdateDto(request);
        DatesDto datesDto = datesService.updateDates(datesUpdateDto, id, member);
        DatesResponse datesResponse = DatesMapper.toDatesResponse(datesDto);
        return ResponseEntity.ok(datesResponse);
    }

    @DeleteMapping("/dates/{id}")
    public ResponseEntity<Boolean> deleteDates(
        @AuthenticationPrincipal final MemberPrincipal memberPrincipal,
        @PathVariable Long id){
        final Member member = memberPrincipal.getMember();
        boolean result = datesService.deleteDates(id, member);
        return ResponseEntity.ok(result);
    }
}

