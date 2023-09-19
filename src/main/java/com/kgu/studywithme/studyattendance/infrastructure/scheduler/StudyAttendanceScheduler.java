package com.kgu.studywithme.studyattendance.infrastructure.scheduler;

import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.studyattendance.domain.model.StudyAttendance;
import com.kgu.studywithme.studyattendance.domain.repository.StudyAttendanceRepository;
import com.kgu.studywithme.studyweekly.domain.repository.query.StudyWeeklyMetadataRepository;
import com.kgu.studywithme.studyweekly.domain.repository.query.dto.AutoAttendanceAndFinishedWeekly;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.ABSENCE;

@Slf4j
@Component
@RequiredArgsConstructor
public class StudyAttendanceScheduler {
    private final StudyWeeklyMetadataRepository studyWeeklyMetadataRepository;
    private final StudyAttendanceRepository studyAttendanceRepository;
    private final MemberRepository memberRepository;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void processAbsenceCheckScheduler() {
        final Set<Long> absenceParticipantIds = new HashSet<>();
        final List<StudyAttendance> attendances = studyAttendanceRepository.findNonAttendanceInformation();
        final List<AutoAttendanceAndFinishedWeekly> targetWeekly = studyWeeklyMetadataRepository.findAutoAttendanceAndFinishedWeekly();
        log.info("결석 처리 대상 Weekly = {}", targetWeekly);

        targetWeekly.forEach(week -> {
            final Long studyId = week.studyId();
            final int specificWeek = week.week();
            final Set<Long> participantIds = extractNonAttendanceParticipantIds(attendances, studyId, specificWeek);

            if (hasCandidates(participantIds)) {
                absenceParticipantIds.addAll(participantIds);
                studyAttendanceRepository.updateParticipantStatus(studyId, specificWeek, participantIds, ABSENCE);
            }
        });
        log.info("결석 처리 대상자 = {}", absenceParticipantIds);
        memberRepository.applyScoreToAbsenceParticipant(absenceParticipantIds);
    }

    private Set<Long> extractNonAttendanceParticipantIds(
            final List<StudyAttendance> attendances,
            final Long studyId,
            final int week
    ) {
        return attendances.stream()
                .filter(attendance -> attendance.getStudyId().equals(studyId) && attendance.getWeek() == week)
                .map(StudyAttendance::getParticipantId)
                .collect(Collectors.toSet());
    }

    private boolean hasCandidates(final Set<Long> participantIds) {
        return !CollectionUtils.isEmpty(participantIds);
    }
}
