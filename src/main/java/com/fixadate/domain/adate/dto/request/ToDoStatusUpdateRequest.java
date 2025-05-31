package com.fixadate.domain.adate.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ToDoStatusUpdateRequest {
        @NotNull
        Long id;
        @NotBlank
        String toDoStatus;
}
