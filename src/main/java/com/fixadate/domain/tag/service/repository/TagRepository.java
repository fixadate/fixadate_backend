package com.fixadate.domain.tag.service.repository;

import java.util.List;
import java.util.Optional;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.tag.entity.Tag;

public interface TagRepository {

	List<Tag> findTagsByMember(final Member member);

	Optional<Tag> findTagByNameAndMember(final String name, final Member member);

	Tag save(final Tag tag);

	List<Tag> saveAll(final List<Tag> tags);

	void delete(final Tag tag);
}
