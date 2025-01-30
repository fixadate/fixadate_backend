package com.fixadate.domain.dates.service;

import com.fixadate.domain.dates.entity.Grades;
import com.fixadate.domain.dates.entity.TeamMembers;
import com.fixadate.domain.dates.repository.TeamRepository;
import com.fixadate.domain.member.service.repository.MemberRepository;
import com.fixadate.global.dto.GeneralResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamMemberService {
    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public GeneralResponseDto joinByTeamId(Long teamId, String memberId, String gradeStr) {
        try {
            TeamMembers teamMembers = TeamMembers.builder()
                    .team(teamRepository.findById(teamId).orElseThrow())
                    .member(memberRepository.findMemberById(memberId).orElseThrow())
                    .grades(Grades.translateStringToGrades(gradeStr))
                    .updatedBy(null)
                    .build();

            return GeneralResponseDto.create("200", "successfully joined team", teamMembers);
        } catch (Exception e) {
            return GeneralResponseDto.create("0T0M0I0E01", "Team or Member does not exist", null);
        }
    }
}
