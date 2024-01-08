package com.fixadate.domain.adate.repository;

import com.fixadate.domain.adate.entity.Adate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdateRepository extends JpaRepository<Adate, Long> {
    Optional<Adate> findAdateByCalendarId(String calendarId);
}
