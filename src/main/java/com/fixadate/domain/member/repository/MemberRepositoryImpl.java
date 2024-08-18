package com.fixadate.domain.member.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.fixadate.domain.auth.entity.OAuthProvider;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.service.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

	private final MemberJpaRepository memberJpaRepository;
	private final MemberQueryRepository memberQueryRepository;

	@Override
	public Optional<Member> findMemberById(final String id) {
		return memberQueryRepository.findMemberById(id);
	}

	@Override
	public Optional<Member> findMemberByEmail(final String email) {
		return memberQueryRepository.findMemberByEmail(email);
	}

	@Override
	public Optional<Member> findMemberByOauthPlatformAndEmailAndName(
		final OAuthProvider oauthPlatform,
		final String email,
		final String memberName
	) {
		return memberQueryRepository.findMemberByOauthPlatformAndEmailAndName(oauthPlatform, email, memberName);
	}

	@Override
	public Member save(final Member member) {
		return memberJpaRepository.save(member);
	}

	@Override
	public void delete(final Member member) {
		memberJpaRepository.delete(member);
	}
}
