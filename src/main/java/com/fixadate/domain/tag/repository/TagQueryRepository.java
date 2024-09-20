package com.fixadate.domain.tag.repository;

import static com.fixadate.domain.tag.entity.QTag.tag;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.tag.entity.Tag;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TagQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public Optional<Tag> findTagByNameAndMember(final String name, final Member member) {
		return Optional.ofNullable(
			jpaQueryFactory
				.selectFrom(tag)
				.where(
					tag.name.eq(name)
							.and(tag.member.eq(member))
				)
				.fetchOne()
		);
	}

	public List<Tag> findTagsByMember(final Member member) {
		return jpaQueryFactory
			.selectFrom(tag)
			.where(tag.member.eq(member))
			.fetch();
	}
}
