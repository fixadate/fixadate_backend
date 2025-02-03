package com.fixadate.domain.dates.service;

import com.fixadate.domain.auth.entity.BaseEntity;
import com.fixadate.domain.dates.dto.TeamCreateRequest;
import com.fixadate.domain.dates.entity.Grades;
import com.fixadate.domain.dates.entity.TeamMembers;
import com.fixadate.domain.dates.entity.Teams;
import com.fixadate.domain.dates.repository.TeamMembersRepository;
import com.fixadate.domain.dates.repository.TeamRepository;


import com.fixadate.domain.member.entity.Member;
import java.util.List;

import com.fixadate.domain.notification.event.object.TeamDeleteEvent;
import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMembersRepository teamMembersRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public Teams createTeam(Member member, TeamCreateRequest requestDto) {
        // todo: 팀 생성 제한 처리 로직


        Teams team = new Teams();
        team.setName(requestDto.name());
        team.setDescription(requestDto.description());
        Teams createdTeam = teamRepository.save(team);

        TeamMembers owner = TeamMembers.builder()
            .member(member)
            .team(createdTeam)
            .grades(Grades.OWNER)
            .build();

        teamMembersRepository.save(owner);

        // todo: 팀 생성 갯수 처리 로직

        return createdTeam;
    }

    public boolean deleteTeam(Member owner, Long id) {
        // todo: 팀 제거 가능한 사람인지

        Teams foundTeam = teamRepository.findById(id).orElseThrow(
            () -> new RuntimeException("")
        );
        List<TeamMembers> teamMembers = teamMembersRepository.findAllByTeam(foundTeam);

        String teamName = foundTeam.getName();
        List<Member> memberList = teamMembers.stream().map(TeamMembers::getMember).toList();

        // todo: 팀 일정 모두 삭제

        teamMembersRepository.deleteAllByTeam(foundTeam);
        teamMembers.forEach(BaseEntity::delete);
        foundTeam.delete();

        boolean isDeleted = teamRepository.existsByIdAndStatusIs(id, BaseEntity.DataStatus.DELETED);
        if(isDeleted) {
            memberList.forEach(teamMember -> {
                applicationEventPublisher.publishEvent(new TeamDeleteEvent(teamMember, teamName));
            });
        }

        return isDeleted;
    }
}
