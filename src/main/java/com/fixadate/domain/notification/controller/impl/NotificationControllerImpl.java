package com.fixadate.domain.notification.controller.impl;

import com.fixadate.domain.common.dto.SseEventResponse;
import com.fixadate.domain.notification.controller.NotificationController;
import com.fixadate.domain.notification.dto.NotificationPageResponse;
import com.fixadate.domain.notification.service.NotificationService;
import com.fixadate.global.annotation.RestControllerWithMapping;
import com.fixadate.global.dto.GeneralResponseDto;
import com.fixadate.global.factory.PageFactory;
import com.fixadate.global.jwt.MemberPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@RestControllerWithMapping("/v1/alarm")
public class NotificationControllerImpl implements NotificationController {
    private final NotificationService notificationService;

    @Override
    @GetMapping("/list")
    public GeneralResponseDto getNotificationList(
        @AuthenticationPrincipal final MemberPrincipal memberPrincipal,
        @RequestParam("page") int page,
        @RequestParam("size") int size
    ) {
        Pageable newPageable = PageFactory.getPageableSortBy(page, size, "createDate", false);
        return GeneralResponseDto.create("200", "read success", notificationService.getNotificationList(memberPrincipal.getMember(), newPageable));
    }

    @Override
    @PatchMapping("/{id}/status")
    public GeneralResponseDto changeNotificationRead(
        @AuthenticationPrincipal final MemberPrincipal memberPrincipal,
        @PathVariable("id") Long id) {
        return GeneralResponseDto.create("200", "read success", notificationService.changeNotificationRead(id, memberPrincipal.getMember()));
    }

    @Override
    @DeleteMapping("/{id}/status")
    public GeneralResponseDto deleteNotification(
        @AuthenticationPrincipal final MemberPrincipal memberPrincipal,
        @PathVariable("id") Long id) {
        return GeneralResponseDto.create("200", "delete success", notificationService.deleteNotification(id, memberPrincipal.getMember()));
    }

    @Override
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public GeneralResponseDto checkHasAliveNotification(@Parameter(description = "회원 정보") @AuthenticationPrincipal final MemberPrincipal memberPrincipal) {
        // 서비스를 통해 생성된 SseEmitter를 반환
        return GeneralResponseDto.create("200", "subscribe success", notificationService.connectNotification(memberPrincipal.getMember().getId()));
    }
}
