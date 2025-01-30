package com.fixadate.domain.dates.repository;

import com.fixadate.domain.dates.entity.TeamMembers;
import com.fixadate.domain.dates.entity.Teams;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamMembersRepository extends JpaRepository<TeamMembers, Long> {
    List<TeamMembers> findAllByTeam(Teams team);
    void deleteAllByTeam(Teams team);
}
