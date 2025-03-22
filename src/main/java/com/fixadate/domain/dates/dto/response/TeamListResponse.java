package com.fixadate.domain.dates.dto.response;

import com.fixadate.domain.dates.entity.TeamMembers;
import com.fixadate.domain.member.entity.Member;
import java.util.List;

public record TeamListResponse(
    List<Each> teamList){

    public record Each(Long teamId,
                       String teamName,
                       boolean isOwner,
                       String ownerName,
                       int teamMemberCnt,
                       List<TeamMemberList> teamMemberList){}


    public record TeamMemberList(
        Long teamMemberId,
        String memberName,
        String memberProfile
    ){
        public static TeamMemberList of(TeamMembers teamMembers){
            Member member = teamMembers.getMember();
            return new TeamMemberList(
                teamMembers.getId(),
                member.getName(),
                member.getProfileImg());
        }
    }
}
