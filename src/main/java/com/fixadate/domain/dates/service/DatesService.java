package com.fixadate.domain.dates.service;

import static com.fixadate.global.exception.ExceptionCode.CONFLICT_DATES_COLLECTION_WITH_ADATE;
import static com.fixadate.global.exception.ExceptionCode.CONFLICT_DATES_COLLECTION_WITH_DATES;
import static com.fixadate.global.exception.ExceptionCode.INVALID_ACCESS_DATES;
import static com.fixadate.global.exception.ExceptionCode.INVALID_ACCESS_DATES_COLLECTION;
import static com.fixadate.global.exception.ExceptionCode.INVALID_START_END_TIME;
import static com.fixadate.global.exception.ExceptionCode.NOT_FOUND_DATES_COORDINATION_ID;
import static com.fixadate.global.exception.ExceptionCode.NOT_FOUND_DATES_ID;
import static com.fixadate.global.exception.ExceptionCode.NOT_FOUND_MEMBER_ID;
import static com.fixadate.global.exception.ExceptionCode.NOT_FOUND_TEAM_ID;
import static com.fixadate.global.exception.ExceptionCode.NOT_PROPONENT;
import static com.fixadate.global.util.TimeUtil.getLocalDateTimeFromYearAndMonth;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.service.repository.AdateRepository;
import com.fixadate.domain.auth.entity.BaseEntity.DataStatus;
import com.fixadate.domain.dates.dto.CalendarDto;
import com.fixadate.domain.dates.dto.DatesCoordinationDto;
import com.fixadate.domain.dates.dto.DatesCoordinationRegisterDto;
import com.fixadate.domain.dates.dto.DatesDto;
import com.fixadate.domain.dates.dto.DatesRegisterDto;
import com.fixadate.domain.dates.dto.DatesUpdateDto;
import com.fixadate.domain.dates.dto.request.ChoiceDatesRequest;
import com.fixadate.domain.dates.dto.response.*;
import com.fixadate.domain.dates.dto.response.DatesCollectionsResponse.DatesCollectionDateInfo;
import com.fixadate.domain.dates.dto.response.DatesDetailResponse.DatesParticipants;
import com.fixadate.domain.dates.dto.response.DatesDetailResponse.Proponent;
import com.fixadate.domain.dates.dto.response.DatesInfoResponse.DailyDatesInfo;
import com.fixadate.domain.dates.dto.response.GetDatesConfirmResponse.DatesCollectionsInfo;
import com.fixadate.domain.dates.entity.Dates;
import com.fixadate.domain.dates.entity.DatesCoordinationMembers;
import com.fixadate.domain.dates.entity.DatesCoordinations;
import com.fixadate.domain.dates.entity.DatesMembers;
import com.fixadate.domain.dates.entity.Grades;
import com.fixadate.domain.dates.entity.TeamMembers;
import com.fixadate.domain.dates.entity.Teams;
import com.fixadate.domain.dates.mapper.DatesMapper;
import com.fixadate.domain.dates.repository.DatesCoordinationMembersRepository;
import com.fixadate.domain.dates.repository.DatesCoordinationsRepository;
import com.fixadate.domain.dates.repository.DatesMembersRepository;
import com.fixadate.domain.dates.repository.DatesQueryRepository;
import com.fixadate.domain.dates.repository.DatesRepository;
import com.fixadate.domain.dates.repository.TeamMembersRepository;
import com.fixadate.domain.dates.repository.TeamRepository;
import com.fixadate.domain.main.dto.DatesMemberInfo;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.service.repository.MemberRepository;
import com.fixadate.domain.notification.event.object.DatesCoordinationCancelEvent;
import com.fixadate.domain.notification.event.object.DatesCoordinationChoiceEvent;
import com.fixadate.domain.notification.event.object.DatesCoordinationConfirmEvent;
import com.fixadate.domain.notification.event.object.DatesCoordinationCreateEvent;
import com.fixadate.domain.notification.event.object.DatesCreateEvent;
import com.fixadate.domain.notification.event.object.DatesDeleteEvent;
import com.fixadate.global.dto.GeneralResponseDto;
import com.fixadate.global.exception.badrequest.InvalidTimeException;
import com.fixadate.global.exception.forbidden.ForbiddenException;
import com.fixadate.global.exception.notfound.NotFoundException;
import com.fixadate.global.util.TimeUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
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
    private final DatesMembersRepository datesMembersRepository;
    private final DatesCoordinationsRepository datesCoordinationsRepository;
    private final DatesCoordinationMembersRepository datesCoordinationMembersRepository;
    private final AdateRepository adateRepository;
    private final MemberRepository memberRepository;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Transactional
    public DatesCoordinationDto createDatesCoordination(DatesCoordinationRegisterDto requestDto, Member member) {
        checkStartAndEndTime(requestDto.startsWhen(), requestDto.endsWhen());
        final Teams foundTeam = teamRepository.findById(requestDto.teamId()).orElseThrow(
            () -> new NotFoundException(NOT_FOUND_TEAM_ID)
        );
        List<TeamMembers> teamMembers = teamMembersRepository.findAllByTeamAndStatusIs(foundTeam, DataStatus.ACTIVE);

        TeamMembers proponent = validateCreateDatesPossible(member, teamMembers);

        teamMembers = teamMembers.stream().filter(each -> requestDto.memberIdList().contains(String.valueOf(each.getId()))).toList();

        final DatesCoordinations createdDatesCoordinations = DatesMapper.toDatesCoordinationEntity(requestDto, member, foundTeam);

        final DatesCoordinations savedDatesCoordinations = datesCoordinationsRepository.save(createdDatesCoordinations);

        for(String datesMemberId : requestDto.memberIdList()){
            Optional<TeamMembers> foundMember = teamMembers.stream()
                .filter(teamMember -> teamMember.getId().equals(Long.valueOf(datesMemberId)))
                .findFirst();
            if(foundMember.isEmpty()){
                throw new RuntimeException("not team member");
            }

            DatesCoordinationMembers datesCoordinationMembers = DatesCoordinationMembers
                .builder()
                .member(foundMember.get())
                .datesCoordinations(savedDatesCoordinations)
                .grades(foundMember.get().getGrades())
                .build();
            datesCoordinationMembersRepository.save(datesCoordinationMembers);
        }

        List<Member> memberList = teamMembers.stream().map(TeamMembers::getMember).toList();

        DatesCoordinationDto datesCoordinationDto = DatesMapper.toDatesCoordinationDto(savedDatesCoordinations);

        if(savedDatesCoordinations.getId() != null){
            applicationEventPublisher.publishEvent(new DatesCoordinationCreateEvent(memberList, datesCoordinationDto));
        }

        return datesCoordinationDto;
    }

    public DatesDto createDates(DatesRegisterDto requestDto, Member member) {
        final Teams foundTeam = teamRepository.findById(requestDto.teamId()).orElseThrow(
            () -> new RuntimeException("")
        );
        List<TeamMembers> teamMembers = teamMembersRepository.findAllByTeamAndStatusIs(foundTeam, DataStatus.ACTIVE);

//        validateCreateDatesPossible(member, teamMembers);

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

    private TeamMembers validateCreateDatesPossible(Member member, List<TeamMembers> teamMembers) {
        Optional<TeamMembers> foundMember = teamMembers.stream()
            .filter(teamMember -> teamMember.getMember().equals(member))
            .findFirst();
        if(foundMember.isEmpty()){
            throw new RuntimeException("not team member");
        }

        return foundMember.get();
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

    public Dates getDates(Long id) {
        return datesRepository.findByIdAndStatusIs(id, DataStatus.ACTIVE).orElseThrow(
            () -> new NotFoundException(NOT_FOUND_DATES_ID)
        );
    }

    public DatesDetailResponse getDatesDetail(final Dates dates, Member member) {
        // datesParticipants 불러오기
        List<DatesMembers> datesMembers = datesMembersRepository.findAllByDatesAndStatusIs(dates, DataStatus.ACTIVE);

        // datesMember인가
        boolean isParticipant = datesMembers.stream()
            .anyMatch(datesMember -> datesMember.getMember().getId().equals(member.getId()));
        if(!isParticipant){
            throw new ForbiddenException(INVALID_ACCESS_DATES);
        }
        // 제안자인가
        boolean isProponent = dates.getProponent().getId().equals(member.getId());

        List<DatesParticipants> datesParticipants = datesMembers.stream()
            .map(DatesParticipants::of)
            .toList();

        return DatesDetailResponse.of(dates, datesParticipants, isProponent);
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

    // 너무 많은 데이터 조회
    public DatesInfoResponse getDatesByStartAndEndTime(
        final Member member,
        final LocalDateTime startDateTime,
        final LocalDateTime endDateTime
    ) {
        checkStartAndEndTime(startDateTime, endDateTime);
        final List<Dates> datesList = findByDateRange(member, startDateTime, endDateTime);
        List<DatesResponse> datesInfos = new ArrayList<>();
        String myMemberId = member.getId();

        // datesMember 세팅
        for(Dates dates : datesList) {
            List<DatesMembers> datesMembers = datesMembersRepository.findAllByDatesAndStatusIs(dates, DataStatus.ACTIVE);
            List<DatesMemberInfo> datesMemberInfos = datesMembers.stream().map(each -> DatesMemberInfo.of(each, myMemberId)).toList();
            datesInfos.add(DatesResponse.of(member, dates, datesMemberInfos));
        }

        DatesInfoResponse dateInfos = new DatesInfoResponse();
        dateInfos.setDateInfos(startDateTime, endDateTime);

        for(DailyDatesInfo dateInfo : dateInfos.getDateList()) {
            List<DatesResponse> myScheduleInfosByDate = new ArrayList<>();
            List<DatesResponse> otherScheduleInfosByDate = new ArrayList<>();
            for(DatesResponse datesInfo : datesInfos) {
                if(datesInfo.startDate().equals(dateInfo.getDate()) ||
                    (LocalDate.parse(dateInfo.getDate(), dateFormatter).atStartOfDay().isAfter(LocalDateTime.parse(datesInfo.startsWhen(), formatter)))
                        && LocalDateTime.parse(datesInfo.endsWhen(), formatter).isAfter(LocalDate.parse(dateInfo.getDate(), dateFormatter).atStartOfDay())) {

                    if(datesInfo.datesMemberList().stream().anyMatch(DatesMemberInfo::isMe)) {
                        myScheduleInfosByDate.add(datesInfo);
                    } else {
                        otherScheduleInfosByDate.add(datesInfo);
                    }
                    myScheduleInfosByDate.add(datesInfo);
                    otherScheduleInfosByDate.add(datesInfo);
                }
            }

            dateInfo.setMyScheduleList(myScheduleInfosByDate);
            dateInfo.setOtherScheduleList(otherScheduleInfosByDate);

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

    public DatesCoordinations getDatesCoordination(Member member, Long id) {
        DatesCoordinations datesCoordinations = datesCoordinationsRepository.findById(id).orElseThrow(
            () -> new NotFoundException(NOT_FOUND_DATES_COORDINATION_ID)
        );

        if(DataStatus.DELETED.equals(datesCoordinations.getStatus())){
            return null;
        }

        return datesCoordinations;
    }

    public DatesCollectionsResponse getDatesCollections(Member member, DatesCoordinations datesCoordinations) {
        List<DatesCoordinationMembers> datesCoordinationMembers = datesCoordinationMembersRepository.findAllByDatesCoordinationsAndStatusIs(datesCoordinations, DataStatus.ACTIVE);

        // 참여자인지 확인
        Optional<DatesCoordinationMembers> foundMember = datesCoordinationMembers.stream()
            .filter(datesCoordinationMember -> datesCoordinationMember.getMember().getMember().getId().equals(member.getId()))
            .findFirst();
        if(foundMember.isEmpty()){
            throw new RuntimeException("not dates collection member");
        }

        List<DatesCollectionDateInfo> dateInfos = datesCoordinationMembers.stream()
            .map(datesCoordinationMember -> {
                return new DatesCollectionDateInfo(datesCoordinationMember, member.getId().equals(datesCoordinationMember.getMember().getMember().getId()));
            })
            .collect(Collectors.toList());


        List<DatesCollectionDateInfo> myDateInfos = new ArrayList<>();
        List<Adate> myAdates = adateRepository.findOverlappingAdates(member, datesCoordinations.getStartsWhen(), datesCoordinations.getEndsWhen());
        List<Dates> myDates = datesQueryRepository.findOverlappingDates(member, datesCoordinations.getStartsWhen(), datesCoordinations.getEndsWhen());

        for(Adate adate : myAdates){
            myDateInfos.add(new DatesCollectionDateInfo(adate));
        }

        for(Dates date : myDates){
            myDateInfos.add(new DatesCollectionDateInfo(date));
        }

        int totalMemberCnt = datesCoordinationMembersRepository.countAllByDatesCoordinationsAndStatusIs(datesCoordinations, DataStatus.ACTIVE);

        return DatesCollectionsResponse.builder()
            .datesCoordinationId(datesCoordinations.getId())
            .title(datesCoordinations.getTitle())
            .time(TimeUtil.convertMinutesToTime(datesCoordinations.getMinutes()))
            .totalMemberCnt(totalMemberCnt)
            .startsWhen(datesCoordinations.getStartsWhen().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")))
            .endWhen(datesCoordinations.getEndsWhen().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")))
            .collectionChoiceInfos(dateInfos)
            .myDateInfos(myDateInfos)
            .build();
    }

    public GeneralResponseDto choiceDates(Member member, DatesCoordinations datesCoordinations, ChoiceDatesRequest choiceDatesRequest) {

        List<DatesCoordinationMembers> datesCoordinationMembers = datesCoordinationMembersRepository.findAllByDatesCoordinationsAndStatusIs(datesCoordinations, DataStatus.ACTIVE);

        // 참여자인지 확인
        Optional<DatesCoordinationMembers> foundMember = datesCoordinationMembers.stream()
            .filter(datesCoordinationMember -> datesCoordinationMember.getMember().getMember().getId().equals(member.getId()))
            .findFirst();
        if(foundMember.isEmpty()){
            return GeneralResponseDto.fail(INVALID_ACCESS_DATES_COLLECTION);
        }

        // 충돌하는 일정이 있는지 확인
        LocalDateTime startsWhen = LocalDateTime.parse(choiceDatesRequest.startsWhen(), formatter);
        LocalDateTime endsWhen = LocalDateTime.parse(choiceDatesRequest.endsWhen(), formatter);
        List<Adate> myAdates = adateRepository.findOverlappingAdates(member, startsWhen, endsWhen);
        List<Dates> myDates = datesQueryRepository.findOverlappingDates(member, startsWhen, endsWhen);

        if(myAdates.size() > 0){
            Adate firstAdate = myAdates.get(0);
            CalendarDto calendar = new CalendarDto(firstAdate.getTitle(), firstAdate.getStartsWhen().format(formatter), firstAdate.getEndsWhen().format(formatter), true);
            return GeneralResponseDto.fail(String.valueOf(CONFLICT_DATES_COLLECTION_WITH_ADATE.getCode()), CONFLICT_DATES_COLLECTION_WITH_ADATE.getMessage(), calendar);
        }

        if(myDates.size() > 0){
            Dates firstDates = myDates.get(0);
            CalendarDto calendar = new CalendarDto(firstDates.getTitle(), firstDates.getStartsWhen().format(formatter), firstDates.getEndsWhen().format(formatter), false);
            return GeneralResponseDto.fail(String.valueOf(CONFLICT_DATES_COLLECTION_WITH_DATES.getCode()), CONFLICT_DATES_COLLECTION_WITH_DATES.getMessage(), calendar);
        }

        // 일정 선택
        foundMember.get().choiceDates(choiceDatesRequest.startsWhen(), choiceDatesRequest.endsWhen());

        // todo: 어느정도 기준이 충족되면 일정 조율 알림 발송
        List<DatesCoordinationMembers> updatedDatesCoordinationMembers = datesCoordinationMembersRepository.findAllByDatesCoordinationsAndStatusIs(datesCoordinations, DataStatus.ACTIVE);

        if(updatedDatesCoordinationMembers.stream().allMatch(DatesCoordinationMembers::isChosen)){
            Member proponent = memberRepository.findMemberById(datesCoordinations.getProponentId()).orElseThrow(
                () -> new NotFoundException(NOT_FOUND_MEMBER_ID)
            );
            datesCoordinations.choose();
            // 일정 조율 알림 발송
            applicationEventPublisher.publishEvent(new DatesCoordinationChoiceEvent(proponent, DatesMapper.toDatesCoordinationDto(datesCoordinations)));
        }
        return GeneralResponseDto.success("", true);
    }

    public List<TeamListResponse.TeamMemberList> getInvitableMemberByTeam(Member member, Long teamId) {
        final Teams foundTeam = teamRepository.findById(teamId).orElseThrow(
            () -> new NotFoundException(NOT_FOUND_TEAM_ID)
        );
        List<TeamMembers> teamMembers = teamMembersRepository.findAllByTeamAndStatusIs(foundTeam, DataStatus.ACTIVE);

        teamMembers = teamMembers.stream()
            .filter(teamMember -> !teamMember.getMember().getId().equals(member.getId()))
            .toList();

        return teamMembers.stream()
            .filter(teamMember -> !teamMember.getMember().getId().equals(member.getId()))
            .map(TeamListResponse.TeamMemberList::of)
            .collect(Collectors.toList());
    }

    public GetDatesConfirmResponse getDatesConfirm(Member member, DatesCoordinations datesCoordinations) {
        // 제안자인가
        boolean isProponent = datesCoordinations.getProponentId().equals(member.getId());
        if(!isProponent){
            throw new ForbiddenException(NOT_PROPONENT);
        }

        List<DatesCoordinationMembers> datesCoordinationMembers = datesCoordinationMembersRepository.findAllByDatesCoordinationsAndStatusIs(datesCoordinations, DataStatus.ACTIVE);
        int totalMemberCnt = datesCoordinationMembers.size();
        int minutes = datesCoordinations.getMinutes();

        // 그룹핑
        Map<String, Long> grouped = datesCoordinationMembers.stream()
            .collect(Collectors.groupingBy(
                td -> td.getStartsWhen() + "::" + td.getEndsWhen(),
                Collectors.counting()
            ));

        // 결과 리스트로 변환
        List<DatesCollectionsInfo> datesCollectionsInfoList = grouped.entrySet().stream()
            .map(entry -> {
                String[] parts = entry.getKey().split("::");
                return new DatesCollectionsInfo(parts[0], parts[1], entry.getValue().intValue());
            })
            .toList();

        return new GetDatesConfirmResponse(
            totalMemberCnt,
            minutes,
            datesCollectionsInfoList
        );
    }

    public boolean confirmDatesCoordinations(Member member, DatesCoordinations datesCoordinations) {
        // 제안자인가
        boolean isProponent = datesCoordinations.getProponentId().equals(member.getId());
        if(!isProponent){
            throw new ForbiddenException(NOT_PROPONENT);
        }

        // 일정 조율 완료
        datesCoordinations.confirm();

        // Dates 생성
        Dates createdDates = DatesMapper.toEntity(datesCoordinations, member);
        datesRepository.save(createdDates);


        // 일정 결정 알림 발송
        List<DatesCoordinationMembers> datesCoordinationMembers = datesCoordinationMembersRepository.findAllByDatesCoordinationsAndStatusIs(datesCoordinations, DataStatus.ACTIVE);
        for(DatesCoordinationMembers datesCoordinationMember : datesCoordinationMembers) {
            applicationEventPublisher.publishEvent(
                new DatesCoordinationConfirmEvent(datesCoordinationMember.getMember().getMember(),
                    DatesMapper.toDatesDto(createdDates)));
        }

        return true;
    }

    public boolean cancelDatesCoordinations(Member member, DatesCoordinations datesCoordinations) {
        // 제안자인가
        boolean isProponent = datesCoordinations.getProponentId().equals(member.getId());
        if(!isProponent){
            throw new ForbiddenException(NOT_PROPONENT);
        }

        // 일정 취소
        datesCoordinations.cancel();

        // 일정 취소 알림 발송
        List<DatesCoordinationMembers> datesCoordinationMembers = datesCoordinationMembersRepository.findAllByDatesCoordinationsAndStatusIs(datesCoordinations, DataStatus.ACTIVE);
        for(DatesCoordinationMembers datesCoordinationMember : datesCoordinationMembers){
            applicationEventPublisher.publishEvent(new DatesCoordinationCancelEvent(datesCoordinationMember.getMember().getMember(), DatesMapper.toDatesCoordinationDto(datesCoordinations)));
        }
        return true;
    }

    public MoreDatesInfoResponse getMoreMineDates(Member member, String todayDate) {
        LocalDateTime todayDateTime = LocalDate.parse(todayDate, dateFormatter).atStartOfDay();
        LocalDateTime twoWeeksBeforeDateTime = todayDateTime.minusDays(14);
        LocalDateTime twoWeeksAfterDateTime = todayDateTime.plusDays(14);
        List<Dates> datesList = datesQueryRepository.findByDateRange(member, twoWeeksBeforeDateTime, twoWeeksAfterDateTime);


        return null;
    }

    public MoreDatesInfoResponse getMoreOthersDates(Member member, String todayDate) {
        return null;
    }
}
