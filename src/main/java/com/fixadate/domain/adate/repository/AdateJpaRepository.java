package com.fixadate.domain.adate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fixadate.domain.adate.entity.Adate;

@Repository
public interface AdateJpaRepository extends JpaRepository<Adate, Long> {

}
