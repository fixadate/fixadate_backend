package com.fixadate.domain.dates.controller.impl;


import com.fixadate.domain.dates.controller.DatesCoordinationController;
import com.fixadate.domain.dates.service.DatesCoordinationService;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.annotation.RestControllerWithMapping;
import com.fixadate.global.dto.GeneralResponseDto;
import com.fixadate.global.jwt.MemberPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestControllerWithMapping("/v1/teams/coordinations")
public class DatesCoordinationControllerImpl implements DatesCoordinationController {

    private final DatesCoordinationService datesCoordinationService;

    @Override
    @GetMapping(value = "/{id}/collections")
    public GeneralResponseDto getDatesCollectionList(@PathVariable final Long id,
        @AuthenticationPrincipal final MemberPrincipal memberPrincipal){

        final Member member = memberPrincipal.getMember();
        datesCoordinationService.getDatesCollectionList();
        return GeneralResponseDto.success("", null);
    }

    @Override
    @PostMapping("/{id}/collections")
    public GeneralResponseDto setDatesCollection(@PathVariable final Long id,
        @AuthenticationPrincipal final MemberPrincipal memberPrincipal){

        final Member member = memberPrincipal.getMember();
        datesCoordinationService.setDatesCollection();
        return GeneralResponseDto.success("", null);
    }

    @Override
    @PostMapping("/{id}")
    public GeneralResponseDto confirmDatesCoordination(@PathVariable final Long id,
        @AuthenticationPrincipal final MemberPrincipal memberPrincipal){

        final Member member = memberPrincipal.getMember();
        datesCoordinationService.confirmDatesCoordination();
        return GeneralResponseDto.success("", null);
    }

    @Override
    @GetMapping("/{id}")
    public GeneralResponseDto getDatesCollectionsResult(@PathVariable final Long id,
        @AuthenticationPrincipal final MemberPrincipal memberPrincipal){

        final Member member = memberPrincipal.getMember();
        datesCoordinationService.getDatesCollectionsResult();
        return GeneralResponseDto.success("", null);
    }
}

