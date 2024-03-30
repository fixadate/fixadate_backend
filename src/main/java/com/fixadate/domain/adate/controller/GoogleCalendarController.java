package com.fixadate.domain.adate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

@Tag(name = "GoogleCalendarController", description = "Google API를 관리하는 Controller입니다.")
public interface GoogleCalendarController {
    @Operation(summary = "이벤트 조회", description = "일정 기간의 Adate를 조회합니다.")
    ResponseEntity<Void> getGoogleCalendarEvents(HttpServletRequest httpServletRequest);

//    @Operation(summary = "Google 캘린더 이벤트 등록", description = "Google 캘린더에 이벤트를 등록합니다.")
//    ResponseEntity<Void> registerGoogleCalendarEvents(List<GoogleCalendarRegistRequest> registRequest, MemberPrincipal memberPrincipal);
}
