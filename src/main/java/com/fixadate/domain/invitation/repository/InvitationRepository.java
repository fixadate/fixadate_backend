package com.fixadate.domain.invitation.repository;

import com.fixadate.domain.invitation.entity.Invitation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvitationRepository extends CrudRepository<Invitation, String> {
    Optional<Invitation> findByTeamId(Long teamId);

    Optional<Invitation> findById(String invitationId);
}
