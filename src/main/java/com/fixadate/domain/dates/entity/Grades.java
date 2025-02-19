package com.fixadate.domain.dates.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Grades {
    OWNER("OWNER"),
    MANAGER("MANAGER"),
    MEMBER("MEMBER");
    private final String grades;

    //Sets dafault grade as member
    public static Grades translateStringToGrades(String grades) {
        return switch (grades.toLowerCase()) {
            case "owner" -> Grades.OWNER;
            case "manager" -> Grades.MANAGER;
            case "member" -> Grades.MEMBER;
            default -> throw new IllegalArgumentException("Grades not legal");
        };
    }

}
