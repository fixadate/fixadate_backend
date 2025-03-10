package com.fixadate.domain.dates.dto.response;

import com.fixadate.domain.dates.entity.TeamMembers;
import com.fixadate.domain.member.entity.Member;
import java.util.List;

public record TeamListResponse(
    Long teamId,
    String teamName,
    boolean isOwner,
    List<TeamMemberList> teamMemberList){


    public record TeamMemberList(
        String memberId,
        String memberName,
        String memberProfile,
        String oauthId
    ){
        public TeamMemberList of(TeamMembers teamMembers){
            Member member = teamMembers.getMember();
            return new TeamMemberList(
                member.getId(),
                member.getName(),
                member.getProfileImg(),
                member.getOauthId()
            );
        }
    }
}
