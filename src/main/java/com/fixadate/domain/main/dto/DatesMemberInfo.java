package com.fixadate.domain.main.dto;

import com.fixadate.domain.dates.entity.DatesMembers;
import com.fixadate.domain.dates.entity.Grades;
import com.fixadate.domain.dates.entity.TeamMembers;
import com.fixadate.domain.member.entity.Member;

public record DatesMemberInfo(
		Long id,
		String profileImg,
		String name,
		String nickname,
		String email,
		Grades grades,
		boolean isMe
	){
		public static DatesMemberInfo of(DatesMembers datesMembers, String memberId) {
			Member member = datesMembers.getMember();
			return new DatesMemberInfo(
				datesMembers.getId(),
				member.getProfileImg(),
				member.getName(),
				member.getNickname(),
				member.getEmail(),
				datesMembers.getGrades(),
				datesMembers.getMember().getId().equals(memberId)
			);
		}
}