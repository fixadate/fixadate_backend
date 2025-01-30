package com.fixadate.domain.invitation.repository;

import com.fixadate.domain.invitation.entity.Invitation;
import com.fixadate.domain.invitation.entity.InvitationHistory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface InvitationHistoryRepository extends JpaRepository<InvitationHistory, String> {
}
