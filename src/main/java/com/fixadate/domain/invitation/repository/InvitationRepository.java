package com.fixadate.domain.invitation.repository;

import com.fixadate.domain.invitation.entity.Invitation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvitationRepository extends CrudRepository<Invitation, String> {
    Optional<Invitation> findByTeam(String teamId);

    Optional<Invitation> findInvitationByInvitee(String inviteeEmail);
}
