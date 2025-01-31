package com.fixadate.domain.dates.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Grades {
    OWNER("OWNER"),
    ADMIN("ADMIN"),
    MEMBER("MEMBER");
    private final String grades;

    //Sets dafault grade as member
    public static Grades translateStringToGrades(String grades) {
        return switch (grades.toLowerCase()) {
            case "owner" -> Grades.OWNER;
            case "admin" -> Grades.ADMIN;
            case "member" -> Grades.MEMBER;
            default -> throw new IllegalArgumentException();
        };
    }

}
