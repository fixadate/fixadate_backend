package com.fixadate.domain.adate.repository;

import com.fixadate.domain.adate.entity.Adate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdateRepository extends JpaRepository<Adate, Long>   {
}
