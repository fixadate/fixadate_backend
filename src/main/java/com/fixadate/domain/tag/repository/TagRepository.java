package com.fixadate.domain.tag.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.tag.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {
	List<Tag> findTagsByMember(Member member);

	Optional<Tag> findTagByNameAndMember(String name, Member member);
}
