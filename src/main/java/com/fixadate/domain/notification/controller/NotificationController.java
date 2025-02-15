package com.fixadate.domain.notification.controller;

import com.fixadate.domain.notification.service.NotificationService;
import com.fixadate.global.dto.GeneralResponseDto;
import com.fixadate.global.factory.PageFactory;
import com.fixadate.global.jwt.MemberPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@Tag(name = "Alarm", description = "푸시 알림 API")
@RequestMapping("/alarm")
public class NotificationController {
    private final NotificationService notificationService;

    @Tag(name = "Alarm")
    @Operation(summary = "내 푸시 알림 목록 조회", description = "")
    @GetMapping("/list")
    public GeneralResponseDto getAlarmList(
        @AuthenticationPrincipal final MemberPrincipal memberPrincipal,
        @RequestParam("page") int page,
        @RequestParam("size") int size
    ) {
        Pageable newPageable = PageFactory.getPageableSortBy(page, size, "createdDate", false);
        return GeneralResponseDto.create("200", "read success", notificationService.getNotificationList(memberPrincipal.getMember(), newPageable));
    }

    @Tag(name = "Alarm")
    @Operation(summary = "푸시 알림 읽음", description = "")
    @PatchMapping("/{id}/status")
    public GeneralResponseDto changeAlarmRead(
        @AuthenticationPrincipal final MemberPrincipal memberPrincipal,
        @PathVariable("id") Long id) {
        return GeneralResponseDto.create("200", "read success", notificationService.changeAlarmRead(id, memberPrincipal.getMember()));
    }

    @Tag(name = "Alarm")
    @Operation(summary = "푸시 알림 삭제", description = "")
    @DeleteMapping("/{id}/status")
    public GeneralResponseDto deleteAlarm(
        @AuthenticationPrincipal final MemberPrincipal memberPrincipal,
        @PathVariable("id") Long id) {
        return GeneralResponseDto.create("200", "delete success", notificationService.deleteAlarm(id, memberPrincipal.getMember()));
    }
}
