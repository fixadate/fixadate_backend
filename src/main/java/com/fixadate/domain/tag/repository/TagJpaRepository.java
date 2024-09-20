package com.fixadate.domain.tag.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fixadate.domain.tag.entity.Tag;

public interface TagJpaRepository extends JpaRepository<Tag, Long> {
}
