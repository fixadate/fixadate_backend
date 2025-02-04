package com.fixadate.domain.dates.repository;

import com.fixadate.domain.auth.entity.BaseEntity.DataStatus;
import com.fixadate.domain.dates.entity.TeamMembers;
import com.fixadate.domain.dates.entity.Teams;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamMembersRepository extends JpaRepository<TeamMembers, Long> {
    List<TeamMembers> findAllByTeamAndStatusIs(Teams team, DataStatus status);
    void deleteAllByTeam(Teams team);
    Optional<TeamMembers> findByTeam_IdAndMember_Id(Long teamId, String memberId);

}
