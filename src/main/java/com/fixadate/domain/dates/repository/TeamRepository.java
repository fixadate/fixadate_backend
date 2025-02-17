package com.fixadate.domain.dates.repository;

import com.fixadate.domain.auth.entity.BaseEntity;
import com.fixadate.domain.dates.entity.Teams;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Teams, Long> {
    Optional<Teams> findByName(String name);

    boolean existsByIdAndStatusIs(Long id, BaseEntity.DataStatus status);
}
