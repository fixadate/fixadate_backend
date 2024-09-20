package com.fixadate.domain.tag.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.tag.entity.Tag;
import com.fixadate.domain.tag.service.repository.TagRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TagRepositoryImpl implements TagRepository {

	private final TagJpaRepository tagJpaRepository;
	private final TagQueryRepository tagQueryRepository;

	@Override
	public List<Tag> findTagsByMember(final Member member) {
		return tagQueryRepository.findTagsByMember(member);
	}

	@Override
	public Optional<Tag> findTagByNameAndMember(final String name, final Member member) {
		return tagQueryRepository.findTagByNameAndMember(name, member);
	}

	@Override
	public Tag save(final Tag tag) {
		return tagJpaRepository.save(tag);
	}

	@Override
	public List<Tag> saveAll(final List<Tag> tags) {
		return tagJpaRepository.saveAll(tags);
	}

	@Override
	public void delete(final Tag tag) {
		tagJpaRepository.delete(tag);
	}
}
