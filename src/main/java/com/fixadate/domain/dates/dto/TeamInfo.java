package com.fixadate.domain.dates.dto;


public record TeamInfo(
    Long teamId,
    String name,
    String profileImage,
    String description
//    List<TeamMemberList> teamMembers
) {
}
