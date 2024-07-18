package com.fixadate.domain.invitation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.fixadate.domain.invitation.entity.Invitation;

public interface InvitationRepository extends CrudRepository<Invitation, String> {
	Optional<Invitation> findByTeamId(Long teamId);

	List<Invitation> findAllByTeamId(Long teamId);
}
