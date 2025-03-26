package com.fixadate.domain.dates.service;

import static com.fixadate.global.exception.ExceptionCode.INVALID_START_END_TIME;
import static com.fixadate.global.util.TimeUtil.getLocalDateTimeFromYearAndMonth;

import com.fixadate.domain.adate.dto.response.AdateInfoResponse;
import com.fixadate.domain.adate.dto.response.AdateInfoResponse.DailyAdateInfo;
import com.fixadate.domain.adate.dto.response.AdateResponse;
import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.entity.ToDo;
import com.fixadate.domain.auth.entity.BaseEntity.DataStatus;
import com.fixadate.domain.dates.dto.DatesDto;
import com.fixadate.domain.dates.dto.DatesRegisterDto;
import com.fixadate.domain.dates.dto.DatesUpdateDto;
import com.fixadate.domain.dates.dto.response.DatesDetailResponse;
import com.fixadate.domain.dates.dto.response.DatesInfoResponse;
import com.fixadate.domain.dates.dto.response.DatesInfoResponse.DailyDatesInfo;
import com.fixadate.domain.dates.dto.response.DatesResponse;
import com.fixadate.domain.dates.entity.Dates;
import com.fixadate.domain.dates.entity.Grades;
import com.fixadate.domain.dates.entity.TeamMembers;
import com.fixadate.domain.dates.entity.Teams;
import com.fixadate.domain.dates.mapper.DatesMapper;
import com.fixadate.domain.dates.repository.DatesQueryRepository;
import com.fixadate.domain.dates.repository.DatesRepository;
import com.fixadate.domain.dates.repository.TeamMembersRepository;
import com.fixadate.domain.dates.repository.TeamRepository;
import com.fixadate.domain.main.dto.TodoInfo;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.notification.event.object.DatesCreateEvent;
import com.fixadate.domain.notification.event.object.DatesDeleteEvent;
import com.fixadate.global.exception.badrequest.InvalidTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class DatesService {

    private final TeamRepository teamRepository;
    private final TeamMembersRepository teamMembersRepository;
    private final DatesRepository datesRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final DatesQueryRepository datesQueryRepository;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

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

    public DatesInfoResponse getDatesByWeek(Member member, int year, int month, int weekNum) {
        // 주의 첫째 날(일요일) 구하기
        LocalDate firstDay = LocalDate.of(year, month, 1)
            .with(WeekFields.ISO.weekOfWeekBasedYear(), weekNum)
            .with(WeekFields.ISO.dayOfWeek(), 7); // 일요일은 7

        // 주의 마지막 날(토요일) 구하기
        LocalDate lastDay = firstDay.plusDays(6);

        // LocalDateTime으로 변환
        LocalDateTime firstDateTime = firstDay.atStartOfDay();
        LocalDateTime lastDateTime = lastDay.atStartOfDay();

        return getDatesByStartAndEndTime(member, firstDateTime, lastDateTime);
    }

    public DatesInfoResponse getDatesByMonth(Member member, int year, int month) {
        final LocalDateTime startTime = getLocalDateTimeFromYearAndMonth(year, month, true);
        final LocalDateTime endTime = getLocalDateTimeFromYearAndMonth(year, month, false);

        return getDatesByStartAndEndTime(member, startTime, endTime);
    }

    private void checkStartAndEndTime(final LocalDateTime startTime, final LocalDateTime endTime) {
        if (startTime.isAfter(endTime)) {
            throw new InvalidTimeException(INVALID_START_END_TIME);
        }
    }

    // todo: 날짜별로 Dates 세팅하도록 변경 필요. 너무 많은 데이터 조회
    public DatesInfoResponse getDatesByStartAndEndTime(
        final Member member,
        final LocalDateTime startDateTime,
        final LocalDateTime endDateTime
    ) {
        checkStartAndEndTime(startDateTime, endDateTime);
        final List<Dates> datesList = findByDateRange(member, startDateTime, endDateTime);
        List<DatesResponse> datesInfos = datesList.stream().map(dates -> DatesResponse.of(dates, new ArrayList<>())).toList();

        DatesInfoResponse dateInfos = new DatesInfoResponse();
        dateInfos.setDateInfos(startDateTime, endDateTime);

        LocalDateTime now = LocalDateTime.now();

        for(DailyDatesInfo dateInfo : dateInfos.getDateList()) {
            List<DatesResponse> datesInfosByDate = new ArrayList<>();
            for(DatesResponse datesInfo : datesInfos) {
                if(datesInfo.startDate().equals(dateInfo.getDate())) {
                    datesInfosByDate.add(datesInfo);
                }
            }
//            // 이미 진행된 건은 제외
//            if(dateInfo.isToday()){
//                List<DatesResponse> todayAdateInfos = new ArrayList<>(datesInfosByDate);
//                todayAdateInfos.removeIf(datesResponse -> LocalDateTime.parse(datesResponse.endsWhen(), formatter).isBefore(now));
//                dateInfo.setDatesResponseList(todayAdateInfos);
//            }else {
            dateInfo.setDatesResponseList(datesInfosByDate);
//            }

            dateInfo.setTotalCnt();
        }

        return dateInfos;
    }

    List<Dates> findByDateRange(
        final Member member,
        final LocalDateTime startDateTime,
        final LocalDateTime endDateTime
    ){
        return datesQueryRepository.findByDateRange(member, startDateTime, endDateTime);
    };
}
