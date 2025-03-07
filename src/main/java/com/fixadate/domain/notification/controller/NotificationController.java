package com.fixadate.domain.notification.controller;

import com.fixadate.domain.member.dto.response.MemberInfoResponse;
import com.fixadate.domain.notification.dto.NotificationPageResponse;
import com.fixadate.domain.notification.service.NotificationService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@RestController
@RequiredArgsConstructor
@Tag(name = "NotificationController", description = "푸시 알림 API")
@RequestMapping("/alarm")
public class NotificationController {
    private final NotificationService notificationService;

    @Operation(summary = "내 푸시 알림 목록 조회", description = "")
    @Parameters(value = {
        @Parameter(name = "page", description = "페이지 번호입니다.",
            content = @Content(schema = @Schema(implementation = String.class)), in = ParameterIn.QUERY),
        @Parameter(name = "size", description = "페이지 사이즈입니다.",
            content = @Content(schema = @Schema(implementation = String.class)), in = ParameterIn.QUERY)
    })
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "푸시 알림 paging response",
            content = @Content(schema = @Schema(implementation = NotificationPageResponse.class))),
        @ApiResponse(responseCode = "401", description = "jwt 만료되었을 때 생기는 예외",
            content = @Content(schema = @Schema(implementation = Void.class))),
        @ApiResponse(responseCode = "404", description = "securityContext에 있는 값으로 member를 찾을 수 없는 경우",
            content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @GetMapping("/list")
    public GeneralResponseDto getNotificationList(
        @AuthenticationPrincipal final MemberPrincipal memberPrincipal,
        @RequestParam("page") int page,
        @RequestParam("size") int size
    ) {
        Pageable newPageable = PageFactory.getPageableSortBy(page, size, "createdDate", false);
        return GeneralResponseDto.create("200", "read success", notificationService.getNotificationList(memberPrincipal.getMember(), newPageable));
    }

    @Operation(summary = "푸시 알림 읽음", description = "")
    @Parameters(value = {
        @Parameter(name = "id", description = "push 고유번호",
            content = @Content(schema = @Schema(implementation = Long.class)), in = ParameterIn.QUERY)
    })
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "성공인 경우, true",
            content = @Content(schema = @Schema(implementation = Boolean.class))),
        @ApiResponse(responseCode = "401", description = "jwt 만료되었을 때 생기는 예외",
            content = @Content(schema = @Schema(implementation = Void.class))),
        @ApiResponse(responseCode = "404", description = "securityContext에 있는 값으로 member를 찾을 수 없는 경우",
            content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @PatchMapping("/{id}/status")
    public GeneralResponseDto changeNotificationRead(
        @AuthenticationPrincipal final MemberPrincipal memberPrincipal,
        @PathVariable("id") Long id) {
        return GeneralResponseDto.create("200", "read success", notificationService.changeNotificationRead(id, memberPrincipal.getMember()));
    }

    @Operation(summary = "푸시 알림 삭제", description = "")
    @Parameters(value = {
        @Parameter(name = "id", description = "push 고유번호",
            content = @Content(schema = @Schema(implementation = Long.class)), in = ParameterIn.QUERY)
    })
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "성공인 경우, true",
            content = @Content(schema = @Schema(implementation = Boolean.class))),
        @ApiResponse(responseCode = "401", description = "jwt 만료되었을 때 생기는 예외",
            content = @Content(schema = @Schema(implementation = Void.class))),
        @ApiResponse(responseCode = "404", description = "securityContext에 있는 값으로 member를 찾을 수 없는 경우",
            content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @DeleteMapping("/{id}/status")
    public GeneralResponseDto deleteNotification(
        @AuthenticationPrincipal final MemberPrincipal memberPrincipal,
        @PathVariable("id") Long id) {
        return GeneralResponseDto.create("200", "delete success", notificationService.deleteNotification(id, memberPrincipal.getMember()));
    }

    @Operation(summary = "실시간 알림 체크 구독", description = "실시간으로 확인할 알람이 있는지 체크할 수 있도록 SSE emitter를 반환합니다.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", description = "ok",
            content = @Content(schema = @Schema(implementation = MemberInfoResponse.class)))
    })
    @GetMapping("/subscribe")
    public GeneralResponseDto checkHasAliveNotification(@Parameter(description = "회원 정보") @AuthenticationPrincipal final MemberPrincipal memberPrincipal) {
        // 서비스를 통해 생성된 SseEmitter를 반환
        return GeneralResponseDto.create("200", "subscribe success", notificationService.connectNotification(memberPrincipal.getMember().getId()));
    }
}
