package com.fixadate.domain.member.service.repository;

import java.util.Optional;

import com.fixadate.domain.auth.entity.OAuthProvider;
import com.fixadate.domain.member.entity.Member;

public interface MemberRepository {

	Optional<Member> findMemberById(final String id);

	Optional<Member> findMemberByEmail(final String email);

	Optional<Member> findMemberByOauthPlatformAndEmailAndName(
		final OAuthProvider oauthPlatform,
		final String email,
		final String memberName
	);

	void save(Member member);

	void delete(Member member);
}
