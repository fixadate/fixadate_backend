package com.fixadate.domain.adate.controller;

import com.fixadate.domain.adate.dto.request.AdateRegistRequest;
import com.fixadate.domain.adate.dto.response.AdateCalendarEventResponse;
import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.global.jwt.MemberPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "AdateController", description = "AdateController 입니다.")
public interface AdateController {


    @Operation(summary = "Adate 이벤트 등록", description = "Adate 이벤트를 등록합니다.")
    ResponseEntity<Void> registerAdateEvent(AdateRegistRequest adateRegistRequest, MemberPrincipal memberPrincipal);

    @Operation(summary = "Adate 캘린더 이벤트 조회", description = "Adate 캘린더 이벤트를 조회합니다.")
    ResponseEntity<List<AdateCalendarEventResponse>> getAdateCalendarEvents(MemberPrincipal memberPrincipal, LocalDateTime startDateTime, LocalDateTime endDateTime);

    @Operation(summary = "멤버별 Adate 조회", description = "멤버별 Adate를 조회합니다.")
    ResponseEntity<List<Adate>> getAdatesByMemberName(String memberName);
}
