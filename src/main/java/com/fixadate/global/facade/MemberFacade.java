package com.fixadate.global.facade;

import static com.fixadate.global.exception.ExceptionCode.*;

import org.springframework.stereotype.Component;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.repository.MemberRepository;
import com.fixadate.global.exception.notFound.MemberNotFoundException;

import lombok.RequiredArgsConstructor;

/**
 *
 * @author yongjunhong
 * @since 2024. 7. 9.
 */

@Component
@RequiredArgsConstructor
public class MemberFacade {
	private final MemberRepository memberRepository;

	public Member getMemberById(String memberId) {
		return memberRepository.findMemberById(memberId).orElseThrow(() -> new MemberNotFoundException(
			NOT_FOUND_MEMBER_ID));
	}
}
