package com.fixadate.domain.dates.service;


import com.fixadate.domain.auth.entity.BaseEntity;
import com.fixadate.domain.auth.entity.BaseEntity.DataStatus;
import com.fixadate.domain.dates.dto.DatesDto;
import com.fixadate.domain.dates.dto.DatesRegisterDto;
import com.fixadate.domain.dates.dto.DatesUpdateDto;
import com.fixadate.domain.dates.dto.request.TeamCreateRequest;
import com.fixadate.domain.dates.dto.response.TeamListPageResponse;
import com.fixadate.domain.dates.dto.response.TeamListResponse;
import com.fixadate.domain.dates.dto.response.TeamListResponse.TeamListEach;
import com.fixadate.domain.dates.entity.Dates;
import com.fixadate.domain.dates.entity.Grades;
import com.fixadate.domain.dates.entity.TeamMembers;
import com.fixadate.domain.dates.entity.Teams;
import com.fixadate.domain.dates.mapper.DatesMapper;
import com.fixadate.domain.dates.repository.DatesRepository;
import com.fixadate.domain.dates.repository.TeamMembersRepository;
import com.fixadate.domain.dates.repository.TeamRepository;


import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.entity.MemberPlans;
import com.fixadate.domain.member.entity.MemberResources;
import com.fixadate.domain.member.entity.Plans;
import com.fixadate.domain.member.entity.ResourceType;
import com.fixadate.domain.member.service.repository.MemberPlansRepository;
import com.fixadate.domain.member.service.repository.MemberRepository;
import com.fixadate.domain.member.service.repository.MemberResourcesRepository;
import com.fixadate.domain.member.service.repository.PlanResourcesRepository;
import com.fixadate.domain.member.service.repository.PlansRepository;
import com.fixadate.domain.notification.event.object.DatesCreateEvent;
import com.fixadate.domain.notification.event.object.DatesDeleteEvent;
import com.fixadate.domain.notification.event.object.TeamMemberDeleteEvent;
import com.fixadate.global.exception.ExceptionCode;
import com.fixadate.global.exception.notfound.NotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import com.fixadate.domain.notification.event.object.TeamDeleteEvent;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class TeamService {
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final TeamMembersRepository teamMembersRepository;
    private final DatesRepository datesRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final PlansRepository plansRepository;
    private final MemberPlansRepository memberPlansRepository;
    private final PlanResourcesRepository planResourcesRepository;
    private final MemberResourcesRepository memberResourcesRepository;

    @Transactional
    public Teams createTeam(Member member, TeamCreateRequest requestDto) {
//        MemberPlans memberPlan = member.getMemberPlan();
//        if(!memberPlan.isValid()){
//            throw new RuntimeException("member plan invalid");
//        }
//        Plans foundPlan = memberPlan.getPlan();
//        int teamResourceMaxCnt = planResourcesRepository.getResourceMaxCnt(ResourceType.TEAM, foundPlan);
//        MemberResources foundMemberResources = memberResourcesRepository.getMemberResources(member);
//        int currentTeamResourceCnt = foundMemberResources.getResourceCnt(ResourceType.TEAM);
//
//        if(currentTeamResourceCnt + 1 > teamResourceMaxCnt){
//            throw new RuntimeException("team resource limit exceeded");
//        }

        Teams team = new Teams(requestDto.name(), requestDto.profile());
        Teams createdTeam = teamRepository.save(team);

        TeamMembers owner = TeamMembers.builder()
            .member(member)
            .team(createdTeam)
            .grades(Grades.OWNER)
            .build();

        teamMembersRepository.save(owner);

        for(String memberId : requestDto.inviteMemberIdList()){
            Member inviteMember = memberRepository.findMemberById(memberId).orElseThrow(
                () -> new NotFoundException(ExceptionCode.NOT_FOUND_MEMBER_ID)
            );
            TeamMembers teamMember = TeamMembers.builder()
                .member(inviteMember)
                .team(createdTeam)
                .grades(Grades.MEMBER)
                .build();
            teamMembersRepository.save(teamMember);
        }

//        foundMemberResources.plusResources(ResourceType.TEAM, 1);

        return createdTeam;
    }

    public boolean deleteTeam(Member member, Long id) {
        Teams foundTeam = teamRepository.findById(id).orElseThrow(
            () -> new NotFoundException(ExceptionCode.NOT_FOUND_TEAM_ID)
        );

        // 팀 멤버인지 확인
        Optional<TeamMembers> foundMemberOptional = teamMembersRepository.findByTeam_IdAndMember_Id(foundTeam.getId(), member.getId());
        if(foundMemberOptional.isEmpty() || !DataStatus.ACTIVE.equals(foundMemberOptional.get().getStatus())){
            throw new RuntimeException("not team member");
        }
        // 권한 확인
        TeamMembers foundMember = foundMemberOptional.get();
        Grades memberGrade = foundMember.getGrades();
        boolean isAuthorized = Grades.OWNER.equals(memberGrade) || Grades.MANAGER.equals(memberGrade);
        if(!isAuthorized){
            throw new RuntimeException("invalid access");
        }

        List<TeamMembers> teamMembers = teamMembersRepository.findAllByTeamAndStatusIs(foundTeam, DataStatus.ACTIVE);
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
            // 팀 삭제에 따라 리소스 관리
//            MemberResources foundMemberResources = memberResourcesRepository.getMemberResources(member);
//            foundMemberResources.minusResources(ResourceType.TEAM, 1);

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
        if (datesUpdateDto.title() != null) {
            dates.updateTitle(datesUpdateDto.title());
        }
        if (datesUpdateDto.notes() != null) {
            dates.updateNotes(datesUpdateDto.notes());
        }

        dates.updateStartsWhen(datesUpdateDto.startsWhen());
        dates.updateEndsWhen(datesUpdateDto.endsWhen());
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

    public TeamListPageResponse getTeams(Member member, Pageable pageable) {
        // 참여하고 있는 팀 목록 조회
        Page<TeamMembers> myTeamMemberInfos = teamMembersRepository.findAllByMemberAndStatusIs(member, DataStatus.ACTIVE, pageable);

        List<TeamListEach> teamList = new ArrayList<>();
        // for문 돌면서
        for(TeamMembers teamMember : myTeamMemberInfos){
            Teams team = teamMember.getTeam();
            List<TeamMembers> teamMemberList = teamMembersRepository.findAllByTeamAndStatusIs(team, DataStatus.ACTIVE);

            // 정렬하고나서
            teamMemberList = teamMemberList.stream()
                .sorted(Comparator.comparingInt(tm -> {
                    return switch (tm.getGrades()) {
                        case OWNER -> 0;
                        case MANAGER -> 1;
                        case MEMBER -> 2;
                        default -> 3; // 예외적인 경우
                    };
                }))
                .collect(Collectors.toList());

            List<TeamListResponse.TeamMemberList> teamMemberResponseList = new ArrayList<>();
            for(TeamMembers memberInfo : teamMemberList){
                teamMemberResponseList.add(TeamListResponse.TeamMemberList.of(memberInfo));
            }

            // 해당 팀에서 owner 찾기
            TeamMembers owner = teamMemberList.stream()
                .filter(memberInfo -> memberInfo.getGrades().equals(Grades.OWNER))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("owner not found"));

            teamList.add(new TeamListEach(
                team.getId(),
                team.getName(),
                Grades.OWNER.equals(teamMember.getGrades()),
                owner.getMember().getNickname(),
                teamMemberList.size(),
                teamMemberResponseList
            ));
        }

        return new TeamListPageResponse(new PageImpl<>(teamList, pageable, myTeamMemberInfos.getTotalElements()));
    }

    public boolean deleteTeamMember(Member member, Long teamMemberId) {
        TeamMembers targetTeamMember = teamMembersRepository.findByIdAndStatusIs(teamMemberId, DataStatus.ACTIVE).orElseThrow(
            () -> new NotFoundException(ExceptionCode.NOT_FOUND_MEMBER_ID)
        );
        Teams team = targetTeamMember.getTeam();
        Optional<TeamMembers> foundMemberOptional = teamMembersRepository.findByTeam_IdAndMember_Id(team.getId(), member.getId());
        if(foundMemberOptional.isEmpty() || !DataStatus.ACTIVE.equals(foundMemberOptional.get().getStatus())){
            throw new RuntimeException("not team member");
        }
        TeamMembers foundMember = foundMemberOptional.get();
        Grades memberGrade = foundMember.getGrades();
        boolean isAuthorized = Grades.OWNER.equals(memberGrade) || Grades.MANAGER.equals(memberGrade);
        if(!isAuthorized){
            throw new RuntimeException("invalid access");
        }

        targetTeamMember.delete();
        boolean isDeleted = DataStatus.DELETED.equals(targetTeamMember.getStatus());

        // 팀 멤버 추방 알림
        if(isDeleted){
            applicationEventPublisher.publishEvent(new TeamMemberDeleteEvent(targetTeamMember.getMember(), team.getName()));
        }

        return isDeleted;
    }
}
