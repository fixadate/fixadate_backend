package com.fixadate.domain.notification.enumerations;

import lombok.Getter;

@Getter
public enum PushNotificationType {
    NORMAL,                 // 일반
    DATES_MARK_REQUEST,     // 팀 일정 가능 시간 표기 요청
    WORKSPACE_INVITATION,   // 워크 스페이스 초대 알람
    DATES_CHOICE,           // 팀 일정 결정 알람
    DATES_CONFIRMED,        // 팀 일정 확정 알람
    DATES_CANCEL            // 팀 일정 확정 알람
}