package com.fixadate.domain.dates.service;

import com.fixadate.domain.dates.dto.TeamCreateRequest;
import com.fixadate.domain.dates.entity.Teams;
import com.fixadate.domain.dates.repository.TeamRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.http.HttpResponse;

@Service
public class TeamService {

    private final TeamRepository teamRepository;

    @Autowired
    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @Transactional
    public Teams createTeam(TeamCreateRequest requestDto) {
        if (teamRepository.existsByName(requestDto.name())) {
            throw new IllegalArgumentException("이미 존재하는 팀 이름이다.");//////////////////////////////////////
            return ;
        }

        Teams team = new Teams();
        team.setName(requestDto.name());
        team.setDescription(requestDto.description());

        return teamRepository.save(team);
    }
}
