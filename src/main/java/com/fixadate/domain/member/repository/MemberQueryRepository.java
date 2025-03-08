package com.fixadate.domain.member.repository;

import static com.fixadate.domain.member.entity.QMember.member;

import com.fixadate.domain.member.entity.QMember;
import com.querydsl.core.types.dsl.CaseBuilder;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.fixadate.domain.auth.entity.OAuthProvider;
import com.fixadate.domain.member.entity.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public Optional<Member> findMemberById(final String id) {
		return Optional.ofNullable(
			jpaQueryFactory
				.selectFrom(member)
				.where(member.id.eq(id))
				.fetchOne()
		);
	}

	public Optional<Member> findMemberByEmail(final String email) {
		return Optional.ofNullable(
			jpaQueryFactory
				.selectFrom(member)
				.where(member.email.eq(email))
				.fetchOne()
		);
	}

	public Optional<Member> findMemberByOauthPlatformAndEmailAndName(
		final OAuthProvider oauthPlatform,
		final String email,
		final String memberName
	) {
		return Optional.ofNullable(
			jpaQueryFactory
				.selectFrom(member)
				.where(member.oauthPlatform.eq(oauthPlatform)
										   .and(member.email.eq(email))
										   .and(member.name.eq(memberName)))
				.fetchOne()
		);
	}

	public List<Member> findMembersByEmailContainAndExcludingIds(String email, List<String> memberIds){
		QMember member = QMember.member;

		return jpaQueryFactory
			.selectFrom(member)
			.where(
				member.email.contains(email)
					.and(member.id.notIn(memberIds))
			)
			.orderBy(
				new CaseBuilder()
					.when(member.email.startsWith(email)).then(1)
					.otherwise(2).asc(),
				member.email.asc()
			)
			.fetch();
	}

}
