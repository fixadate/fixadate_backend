package com.fixadate.domain.dates.dto.response;

import com.fixadate.domain.dates.dto.response.TeamListResponse.TeamListEach;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.springframework.data.domain.Page;

@Schema(name = "TeamListPageResponse", description = "Page response for teams!!!")
public record TeamListPageResponse(
    List<TeamListEach> content,
    int pageNumber,
    int pageSize,
    long totalElements,
    int totalPages,
    boolean last
) {

    public TeamListPageResponse(Page<TeamListEach> page) {
        this(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

}