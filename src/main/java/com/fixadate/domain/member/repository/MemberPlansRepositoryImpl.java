package com.fixadate.domain.member.repository;

import com.fixadate.domain.member.entity.MemberPlans;
import com.fixadate.domain.member.service.repository.MemberPlansRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberPlansRepositoryImpl implements MemberPlansRepository {

	private final MemberPlansJpaRepository memberPlansJpaRepository;


	@Override
	public MemberPlans save(MemberPlans memberPlans) {
		return memberPlansJpaRepository.save(memberPlans);
	}
}
