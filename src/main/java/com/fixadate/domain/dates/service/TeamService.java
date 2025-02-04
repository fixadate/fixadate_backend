package com.fixadate.domain.dates.service;


import com.fixadate.domain.auth.entity.BaseEntity;
import com.fixadate.domain.auth.entity.BaseEntity.DataStatus;
import com.fixadate.domain.dates.dto.DatesDto;
import com.fixadate.domain.dates.dto.DatesRegisterDto;
import com.fixadate.domain.dates.dto.DatesUpdateDto;
import com.fixadate.domain.dates.dto.request.TeamCreateRequest;
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
import java.util.List;
import java.util.Optional;

import com.fixadate.domain.notification.event.object.TeamDeleteEvent;
import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMembersRepository teamMembersRepository;
    private final DatesRepository datesRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public Teams createTeam(Member member, TeamCreateRequest requestDto) {
        // todo: 팀 생성 제한 처리 로직


        Teams team = new Teams();
        team.setName(requestDto.name());
        team.setDescription(requestDto.description());
        Teams createdTeam = teamRepository.save(team);

        TeamMembers owner = TeamMembers.builder()
            .member(member)
            .team(createdTeam)
            .grades(Grades.OWNER)
            .build();

        teamMembersRepository.save(owner);

        // todo: 팀 생성 갯수 처리 로직

        return createdTeam;
    }

    public boolean deleteTeam(Member member, Long id) {
        Teams foundTeam = teamRepository.findById(id).orElseThrow(
            () -> new RuntimeException("")
        );
        List<TeamMembers> teamMembers = teamMembersRepository.findAllByTeamAndStatusIs(foundTeam, DataStatus.ACTIVE);

        validateDeleteTeamPossible(member, teamMembers);

        String teamName = foundTeam.getName();
        List<Member> memberList = teamMembers.stream().map(TeamMembers::getMember).toList();

        List<Dates> teamCalendarList = datesRepository.findAllByTeamAndStatusIs(foundTeam,
            DataStatus.ACTIVE);
        teamCalendarList.forEach(BaseEntity::delete);

        teamMembersRepository.deleteAllByTeam(foundTeam);
        teamMembers.forEach(BaseEntity::delete);
        foundTeam.delete();

        boolean isDeleted = teamRepository.existsByIdAndStatusIs(id, BaseEntity.DataStatus.DELETED);
        if(isDeleted) {
            memberList.forEach(teamMember -> {
                applicationEventPublisher.publishEvent(new TeamDeleteEvent(teamMember, teamName));
            });
        }

        return isDeleted;
    }

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
        if (datesUpdateDto.tagName() != null) {
//            applicationEventPublisher.publishEvent(new TagSettingEvent(dates, datesUpdateDto.tagName()));
        }
        if (datesUpdateDto.title() != null) {
            dates.updateTitle(datesUpdateDto.title());
        }
        if (datesUpdateDto.notes() != null) {
            dates.updateNotes(datesUpdateDto.notes());
        }
        if (datesUpdateDto.location() != null) {
            dates.updateLocation(datesUpdateDto.location());
        }
        if (datesUpdateDto.alertWhen() != null) {
            dates.updateAlertWhen(datesUpdateDto.alertWhen());
        }
        if (datesUpdateDto.repeatFreq() != null) {
            dates.updateRepeatFreq(datesUpdateDto.repeatFreq());
        }

        dates.updateIfAllDay(datesUpdateDto.ifAllDay());
        dates.updateStartsWhen(datesUpdateDto.startsWhen());
        dates.updateEndsWhen(datesUpdateDto.endsWhen());
        dates.updateReminders(datesUpdateDto.reminders());
    }

    private void validateDeleteTeamPossible(Member member, List<TeamMembers> teamMembers) {
        Optional<TeamMembers> foundMember = teamMembers.stream()
                .filter(teamMember -> teamMember.getMember().equals(member))
                .findFirst();
        if(foundMember.isEmpty()){
            throw new RuntimeException("not team member");
        }
        Grades memberGrade = foundMember.get().getGrades();
        boolean isAuthorized = Grades.OWNER.equals(memberGrade);
        if(!isAuthorized){
            throw new RuntimeException("invalid access");
        }
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

    public void validateTeamMember(Member member, Long teamId) {
        Optional<TeamMembers> foundMember = teamMembersRepository.findByTeam_IdAndMember_Id(teamId, member.getId());
        if(foundMember.isEmpty()){
            throw new RuntimeException("not team member");
        }
    }

    public List<Dates> getTeamDates(Member member, Long teamId) {
        validateTeamMember(member, teamId);
        return datesRepository.findAllByTeam_IdAndStatusIs(teamId, DataStatus.ACTIVE);
    }
}
