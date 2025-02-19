package com.fixadate.domain.adate.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ToDoStatus {
    SCHEDULED("SCHEDULED"),
    DONE("DONE"),
    PROCEEDING("PROCEEDING");
    private final String status;
    public static ToDoStatus translateStringToStatus(String status) {
        return switch (status.toLowerCase()) {
            case "scheduled" -> ToDoStatus.SCHEDULED;
            case "done" -> ToDoStatus.DONE;
            case "proceeding" -> ToDoStatus.PROCEEDING;
            default -> throw new IllegalArgumentException("Status not legal");
        };
    }


}
