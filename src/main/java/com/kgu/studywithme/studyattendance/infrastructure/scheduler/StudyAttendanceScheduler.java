package com.kgu.studywithme.studyattendance.infrastructure.scheduler;

import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.study.infrastructure.repository.query.dto.response.BasicAttendance;
import com.kgu.studywithme.study.infrastructure.repository.query.dto.response.BasicWeekly;
import com.kgu.studywithme.studyattendance.domain.StudyAttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.ABSENCE;

@Component
@RequiredArgsConstructor
public class StudyAttendanceScheduler {
    private final MemberRepository memberRepository;
    private final StudyRepository studyRepository;
    private final StudyAttendanceRepository studyAttendanceRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void processAbsenceCheckScheduler() {
        final Set<Long> absenceParticipantIds = new HashSet<>();
        final List<BasicWeekly> weeks = studyRepository.findAutoAttendanceAndPeriodEndWeek();
        final List<BasicAttendance> attendances = studyRepository.findNonAttendanceInformation();

        weeks.forEach(week -> {
            final Long studyId = week.studyId();
            final int specificWeek = week.week();
            final Set<Long> participantIds = extractNonAttendanceParticipantIds(attendances, studyId, specificWeek);

            if (hasCandidates(participantIds)) {
                absenceParticipantIds.addAll(participantIds);
                studyAttendanceRepository.updateParticipantStatus(studyId, specificWeek, participantIds, ABSENCE);
            }
        });
        memberRepository.applyScoreToAbsenceParticipant(absenceParticipantIds);
    }

    private Set<Long> extractNonAttendanceParticipantIds(
            final List<BasicAttendance> attendances,
            final Long studyId,
            final int week
    ) {
        return attendances.stream()
                .filter(attendance -> attendance.studyId().equals(studyId) && attendance.week() == week)
                .map(BasicAttendance::participantId)
                .collect(Collectors.toSet());
    }

    private boolean hasCandidates(final Set<Long> participantIds) {
        return !CollectionUtils.isEmpty(participantIds);
    }
}
