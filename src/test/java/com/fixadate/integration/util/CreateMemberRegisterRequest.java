package com.fixadate.integration.util;

import java.util.ArrayList;
import java.util.List;

import com.fixadate.domain.auth.dto.request.MemberRegisterRequest;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateMemberRegisterRequest {

	public static List<MemberRegisterRequest> registerMember() {
		List<MemberRegisterRequest> memberRegisterRequests = new ArrayList<>();
		MemberRegisterRequest memberRegisterRequest1 = new MemberRegisterRequest(
			"123",
			"google",
			"hong",
			"213",
			"kevin",
			"20000928",
			"male",
			"student",
			"red",
			"hong@example.com",
			"USER"
		);

		MemberRegisterRequest memberRegisterRequest2 = new MemberRegisterRequest(
			"2",
			"google",
			"muny",
			"314",
			"alex",
			"19980512",
			"female",
			"engineer",
			"blue",
			"muny@example.com",
			"USER"
		);

		MemberRegisterRequest memberRegisterRequest3 = new MemberRegisterRequest(
			"3",
			"google",
			"kim",
			"415",
			"emma",
			"20010320",
			"male",
			"designer",
			"green",
			"kim@example.com",
			"USER"
		);

		MemberRegisterRequest memberRegisterRequest4 = new MemberRegisterRequest(
			"4",
			"google",
			"karina",
			"516",
			"michael",
			"19991225",
			"female",
			"developer",
			"yellow",
			"karina@example.com",
			"USER"
		);

		MemberRegisterRequest memberRegisterRequest5 = new MemberRegisterRequest(
			"5",
			"google",
			"down",
			"617",
			"chris",
			"19921005",
			"female",
			"manager",
			"orange",
			"down@example.com",
			"USER"
		);
		memberRegisterRequests.add(memberRegisterRequest1);
		memberRegisterRequests.add(memberRegisterRequest2);
		memberRegisterRequests.add(memberRegisterRequest3);
		memberRegisterRequests.add(memberRegisterRequest4);
		memberRegisterRequests.add(memberRegisterRequest5);
		return memberRegisterRequests;
	}
}
