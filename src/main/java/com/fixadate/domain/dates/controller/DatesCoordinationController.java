package com.fixadate.domain.dates.controller;

import com.fixadate.global.dto.GeneralResponseDto;
import com.fixadate.global.jwt.MemberPrincipal;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "DatesCoordinationController", description = "DatesCoordinationController 입니다.")
public interface DatesCoordinationController {
    GeneralResponseDto getDatesCollectionList(@PathVariable final Long id,
                                                     @AuthenticationPrincipal final MemberPrincipal memberPrincipal);
    GeneralResponseDto setDatesCollection(@PathVariable final Long id,
                                                 @AuthenticationPrincipal final MemberPrincipal memberPrincipal);

    GeneralResponseDto confirmDatesCoordination(@PathVariable final Long id,
                                                       @AuthenticationPrincipal final MemberPrincipal memberPrincipal);

    GeneralResponseDto getDatesCollectionsResult(@PathVariable final Long id,
                                                        @AuthenticationPrincipal final MemberPrincipal memberPrincipal);
}
