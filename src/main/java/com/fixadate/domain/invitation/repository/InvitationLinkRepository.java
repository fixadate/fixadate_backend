package com.fixadate.domain.invitation.repository;

import com.fixadate.domain.dates.entity.Teams;
import com.fixadate.domain.invitation.entity.Invitation;
import com.fixadate.domain.invitation.entity.InvitationLink;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface InvitationLinkRepository extends JpaRepository<InvitationLink, String> {
	Optional<InvitationLink> findByTeam(Teams team);
	List<InvitationLink> findAllByTeam(Teams team);
	Optional<InvitationLink> findByInviteCode(String inviteCode);
}
