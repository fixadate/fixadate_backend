package com.fixadate.domain.dates.service;

import com.fixadate.domain.auth.entity.BaseEntity;
import com.fixadate.domain.dates.dto.TeamCreateRequest;
import com.fixadate.domain.dates.entity.TeamMembers;
import com.fixadate.domain.dates.entity.TeamMembers.Grades;
import com.fixadate.domain.dates.entity.Teams;
import com.fixadate.domain.dates.repository.TeamMembersRepository;
import com.fixadate.domain.dates.repository.TeamRepository;
import com.fixadate.domain.member.entity.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMembersRepository teamMembersRepository;

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

    public boolean deleteTeam(Member member, Long id) {
        // todo: 팀 제거 가능한 사람인지

        Teams foundTeam = teamRepository.findById(id).orElseThrow(
            () -> new RuntimeException("")
        );
        List<TeamMembers> teamMembers = teamMembersRepository.findAllByTeam(foundTeam);

        String teamName = foundTeam.getName();
        List<Member> memberList = teamMembers.stream().map(TeamMembers::getMember).toList();

        // todo: 팀 일정 모두 삭제

        teamMembersRepository.deleteAllByTeam(foundTeam);
//        List<TeamMembers> teamMembers = teamMembersRepository.findAllByTeam(foundTeam);
//        teamMembers.forEach(BaseEntity::delete);
//        foundTeam.delete();

        teamRepository.delete(foundTeam);

        // todo: 팀 제거 push alarm


        return true;
    }
}
