package com.fixadate.domain.dates.service;

import com.fixadate.domain.auth.entity.BaseEntity.DataStatus;
import com.fixadate.domain.dates.dto.DatesDto;
import com.fixadate.domain.dates.dto.DatesRegisterDto;
import com.fixadate.domain.dates.dto.DatesUpdateDto;
import com.fixadate.domain.dates.dto.response.DatesDetailResponse;
import com.fixadate.domain.dates.entity.Dates;
import com.fixadate.domain.dates.entity.Grades;
import com.fixadate.domain.dates.entity.TeamMembers;
import com.fixadate.domain.dates.entity.Teams;
import com.fixadate.domain.dates.mapper.DatesMapper;
import com.fixadate.domain.dates.repository.DatesRepository;
import com.fixadate.domain.dates.repository.TeamMembersRepository;
import com.fixadate.domain.dates.repository.TeamRepository;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.notification.event.object.DatesCreateEvent;
import com.fixadate.domain.notification.event.object.DatesDeleteEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class DatesService {

    private final TeamRepository teamRepository;
    private final TeamMembersRepository teamMembersRepository;
    private final DatesRepository datesRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public DatesDto createDates(DatesRegisterDto requestDto, Member member) {
        final Teams foundTeam = teamRepository.findById(requestDto.teamId()).orElseThrow(
                () -> new RuntimeException("")
        );
        List<TeamMembers> teamMembers = teamMembersRepository.findAllByTeamAndStatusIs(foundTeam, DataStatus.ACTIVE);

        validateCreateDatesPossible(member, teamMembers);

        final Dates dates = DatesMapper.toEntity(requestDto, member);

        dates.setTeam(foundTeam);
        final Dates savedDates = datesRepository.save(dates);

        List<Member> memberList = teamMembers.stream().map(TeamMembers::getMember).toList();

//        final String tagName = requestDto.tagName();
//        if (tagName != null && !tagName.isEmpty()) {
//            applicationEventPublisher.publishEvent(new TagSettingEvent(dates, tagName));
//        }

        // 팀 일정 새로고침 필요 알림
        if(savedDates.getId() != null){
            applicationEventPublisher.publishEvent(new DatesCreateEvent(memberList, foundTeam));
        }

        return DatesMapper.toDatesDto(savedDates);
    }


    public DatesDto updateDates(DatesUpdateDto datesUpdateDto, Long id, Member member) {
        final Dates dates = datesRepository.findByIdAndStatusIs(id, DataStatus.ACTIVE).orElseThrow(
            () -> new RuntimeException("")
        );
        final Teams foundTeam = dates.getTeam();
        List<TeamMembers> teamMembers = teamMembersRepository.findAllByTeamAndStatusIs(foundTeam, DataStatus.ACTIVE);

        validateUpdateDatesPossible(member, teamMembers);
        updateIfNotNull(dates, datesUpdateDto);

        return DatesMapper.toDatesDto(dates);
    }

    public boolean deleteDates(Long id, Member member) {
        final Dates dates = datesRepository.findByIdAndStatusIs(id, DataStatus.ACTIVE).orElseThrow(
                () -> new RuntimeException("")
        );

        final Teams foundTeam = dates.getTeam();
        List<TeamMembers> teamMembers = teamMembersRepository.findAllByTeamAndStatusIs(foundTeam, DataStatus.ACTIVE);
        validateDeleteDatesPossible(member, teamMembers);

        List<Member> memberList = teamMembers.stream().map(TeamMembers::getMember).toList();

        dates.delete();
        boolean isDeleted = DataStatus.DELETED.equals(dates.getStatus());

        // 팀 일정 새로고침 필요 알림
        if(isDeleted){
            applicationEventPublisher.publishEvent(new DatesDeleteEvent(memberList, foundTeam));
        }

        return isDeleted;
    }

    private void updateIfNotNull(final Dates dates, final DatesUpdateDto datesUpdateDto) {
        if (datesUpdateDto.title() != null) {
            dates.updateTitle(datesUpdateDto.title());
        }
        if (datesUpdateDto.notes() != null) {
            dates.updateNotes(datesUpdateDto.notes());
        }

        dates.updateStartsWhen(datesUpdateDto.startsWhen());
        dates.updateEndsWhen(datesUpdateDto.endsWhen());
    }

    private void validateCreateDatesPossible(Member member, List<TeamMembers> teamMembers) {
        Optional<TeamMembers> foundMember = teamMembers.stream()
                .filter(teamMember -> teamMember.getMember().equals(member))
                .findFirst();
        if(foundMember.isEmpty()){
            throw new RuntimeException("not team member");
        }
        Grades memberGrade = foundMember.get().getGrades();
        boolean isAuthorized = Grades.OWNER.equals(memberGrade) || Grades.MANAGER.equals(memberGrade);
        if(!isAuthorized){
            throw new RuntimeException("invalid access");
        }
    }

    private void validateUpdateDatesPossible(Member member, List<TeamMembers> teamMembers) {
        Optional<TeamMembers> foundMember = teamMembers.stream()
                .filter(teamMember -> teamMember.getMember().equals(member))
                .findFirst();
        if(foundMember.isEmpty()){
            throw new RuntimeException("not team member");
        }
        Grades memberGrade = foundMember.get().getGrades();
        boolean isAuthorized = Grades.OWNER.equals(memberGrade) || Grades.MANAGER.equals(memberGrade);
        if(!isAuthorized){
            throw new RuntimeException("invalid access");
        }
    }

    private void validateDeleteDatesPossible(Member member, List<TeamMembers> teamMembers) {
        Optional<TeamMembers> foundMember = teamMembers.stream()
                .filter(teamMember -> teamMember.getMember().equals(member))
                .findFirst();
        if(foundMember.isEmpty()){
            throw new RuntimeException("not team member");
        }
        Grades memberGrade = foundMember.get().getGrades();
        boolean isAuthorized = Grades.OWNER.equals(memberGrade) || Grades.MANAGER.equals(memberGrade);
        if(!isAuthorized){
            throw new RuntimeException("invalid access");
        }
    }

    public DatesDetailResponse getDatesDetail(Long id, Member member) {
        // teamMember인가
        // Dates 불러오기
        // Owner인가
        // DatesParticipants 불러오기
        return null;
    }

}
