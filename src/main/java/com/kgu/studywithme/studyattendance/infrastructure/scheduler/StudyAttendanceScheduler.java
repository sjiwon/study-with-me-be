package com.kgu.studywithme.studyattendance.infrastructure.scheduler;

import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.studyattendance.application.adapter.StudyAttendanceHandlingRepositoryAdapter;
import com.kgu.studywithme.studyattendance.domain.StudyAttendanceRepository;
import com.kgu.studywithme.studyattendance.infrastructure.query.dto.NonAttendanceWeekly;
import com.kgu.studywithme.studyweekly.application.adapter.StudyWeeklyHandlingRepositoryAdapter;
import com.kgu.studywithme.studyweekly.infrastructure.query.dto.AutoAttendanceAndFinishedWeekly;
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
    private final StudyWeeklyHandlingRepositoryAdapter studyWeeklyHandlingRepositoryAdapter;
    private final StudyAttendanceHandlingRepositoryAdapter studyAttendanceHandlingRepositoryAdapter;
    private final StudyAttendanceRepository studyAttendanceRepository;
    private final MemberRepository memberRepository;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void processAbsenceCheckScheduler() {
        final Set<Long> absenceParticipantIds = new HashSet<>();
        final List<AutoAttendanceAndFinishedWeekly> weeks = studyWeeklyHandlingRepositoryAdapter.findAutoAttendanceAndFinishedWeekly();
        final List<NonAttendanceWeekly> attendances = studyAttendanceHandlingRepositoryAdapter.findNonAttendanceInformation();

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
            final List<NonAttendanceWeekly> attendances,
            final Long studyId,
            final int week
    ) {
        return attendances.stream()
                .filter(attendance -> attendance.studyId().equals(studyId) && attendance.week() == week)
                .map(NonAttendanceWeekly::participantId)
                .collect(Collectors.toSet());
    }

    private boolean hasCandidates(final Set<Long> participantIds) {
        return !CollectionUtils.isEmpty(participantIds);
    }
}
