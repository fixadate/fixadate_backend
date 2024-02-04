package com.fixadate.domain.invitation.repository;

import com.fixadate.domain.invitation.entity.Invitation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface InvitationRepository extends CrudRepository<Invitation, String> {
    Optional<Invitation> findByTeamId(Long teamId);

    List<Invitation> findAllByTeamId(Long teamId);
}
