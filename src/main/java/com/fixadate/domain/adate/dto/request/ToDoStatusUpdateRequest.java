package com.fixadate.domain.adate.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ToDoStatusUpdateRequest {
        @NotBlank
        Long id;
        @NotBlank
        String toDoStatus;
}
