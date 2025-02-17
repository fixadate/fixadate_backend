package com.fixadate.domain.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.springframework.data.domain.Page;

@Schema(name = "NotificationPageResponse", description = "Page response for notifications!!!")
public record NotificationPageResponse(
    List<NotificationListResponse> content,
    int pageNumber,
    int pageSize,
    long totalElements,
    int totalPages,
    boolean last
) {

    public NotificationPageResponse(Page<NotificationListResponse> page) {
        this(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

}
