package com.fixadate.domain.dates.dto.response;

import com.fixadate.domain.common.entity.Calendar;
import com.fixadate.domain.dates.entity.DatesCoordinationMembers;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
public record DatesCollectionsResponse(
        Long datesCoordinationId,
        @Schema(description = "일정 제목")
        String title,
        @Schema(description = "일정 진행 시간")
        String time,
        @Schema(description = "총 일정 참여자")
        int totalMemberCnt,
        @Schema(description = "투표시작일시", example = "yyyyMMddHHmm")
        String startsWhen,
        @Schema(description = "투표종료일시", example = "yyyyMMddHHmm")
        String endWhen,
        @Schema(description = "일정 취합 진행 모음")
        List<DatesCollectionDateInfo> collectionChoiceInfos,
        @Schema(description = "내 일정 모음")
        List<DatesCollectionDateInfo> myDateInfos
    ){
        @Getter
        public static class DatesCollectionDateInfo{
            @Schema(description = "MON")
            private final String startDay;
            @Schema(description = "TUE", example = "혹여 다음날로 넘어가는 일정이 있는 경우")
            private final String endDay;
            @Schema(description = "yyyyMMdd")
            private final String startDate;
            @Schema(description = "yyyyMMdd", example = "혹여 다음날로 넘어가는 일정이 있는 경우")
            private final String endDate;
            private final Long teamMemberId;
            @Schema(description = "yyyyMMddHHmm")
            private final String startsWhen;
            @Schema(description = "yyyyMMddHHmm")
            private final String endsWhen;
            private final boolean isMe;

            public DatesCollectionDateInfo(DatesCoordinationMembers datesCoordinationMembers, boolean isMe){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                LocalDateTime startsWhen = LocalDateTime.parse(datesCoordinationMembers.getStartsWhen(), formatter);
                LocalDateTime endsWhen = LocalDateTime.parse(datesCoordinationMembers.getEndsWhen(), formatter);
                this.startDay = startsWhen.getDayOfWeek().toString().substring(0,3);
                this.endDay = endsWhen.getDayOfWeek().toString().substring(0,3);
                this.startDate = startsWhen.format(dateFormatter);
                this.endDate = endsWhen.format(dateFormatter);
                this.teamMemberId = datesCoordinationMembers.getMember().getId();
                this.startsWhen = datesCoordinationMembers.getStartsWhen();
                this.endsWhen = datesCoordinationMembers.getEndsWhen();
                this.isMe = isMe;
            }

            public DatesCollectionDateInfo(Calendar calendar){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                LocalDateTime startsWhen = calendar.getStartsWhen();
                LocalDateTime endsWhen = calendar.getEndsWhen();
                this.startDay = startsWhen.getDayOfWeek().toString().substring(0,3);
                this.endDay = endsWhen.getDayOfWeek().toString().substring(0,3);
                this.startDate = startsWhen.format(dateFormatter);
                this.endDate = endsWhen.format(dateFormatter);
                this.teamMemberId = null;
                this.startsWhen = startsWhen.format(formatter);
                this.endsWhen = endsWhen.format(formatter);
                this.isMe = true;
            }
        }
    }