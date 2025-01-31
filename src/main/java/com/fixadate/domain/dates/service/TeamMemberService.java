package com.fixadate.domain.dates.service;

import com.fixadate.domain.dates.entity.Grades;
import com.fixadate.domain.dates.entity.TeamMembers;
import com.fixadate.domain.dates.repository.TeamRepository;
import com.fixadate.domain.member.service.repository.MemberRepository;
import com.fixadate.global.dto.GeneralResponseDto;
import com.fixadate.global.exception.CustomException;
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
            Grades grades = Grades.translateStringToGrades(gradeStr);
        } catch (Exception e) {
            GeneralResponseDto.create(CustomException.TeamMemberRoleError001.getCustomCode(), CustomException.TeamMemberRoleError001.getErrorMsg(), null);
        }

        try {

            TeamMembers teamMembers = TeamMembers.builder()
                    .team(teamRepository.findById(teamId).orElseThrow())
                    .member(memberRepository.findMemberById(memberId).orElseThrow())
                    .grades(Grades.translateStringToGrades(gradeStr))
                    .updatedBy(null)
                    .build();

            return GeneralResponseDto.create("200", "successfully joined team", teamMembers);
        } catch (Exception e) {
            return GeneralResponseDto.create(CustomException.TeamMemberInfoError001.getCustomCode(), CustomException.TeamMemberInfoError001.getErrorMsg(), null);
        }
    }
}
