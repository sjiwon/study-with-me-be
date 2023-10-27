package com.kgu.studywithme.studyattendance.domain.service;

import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.studyattendance.domain.model.StudyAttendance;
import com.kgu.studywithme.studyattendance.domain.repository.StudyAttendanceRepository;
import com.kgu.studywithme.studyweekly.domain.repository.query.StudyWeeklyMetadataRepository;
import com.kgu.studywithme.studyweekly.domain.repository.query.dto.AutoAttendanceAndFinishedWeekly;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.ABSENCE;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateWeeklyAttendanceBatchProcessor {
    private final StudyWeeklyMetadataRepository studyWeeklyMetadataRepository;
    private final StudyAttendanceRepository studyAttendanceRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void checkAbsenceParticipantAndApplyAbsenceScore() {
        final LocalDateTime now = LocalDateTime.now();
        final List<StudyAttendance> nonAttendances = studyAttendanceRepository.findNonAttendanceInformation();
        final List<AutoAttendanceAndFinishedWeekly> targetWeekly = studyWeeklyMetadataRepository.findAutoAttendanceAndFinishedWeekly(now.minusDays(2), now);
        log.info("결석 처리 대상 Weekly -> {}", targetWeekly); // 처리 시간을 고려해서 [now-2..now]를 target으로 선정

        targetWeekly.forEach(week -> {
            final Long studyId = week.studyId();
            final int specificWeek = week.week();
            final Set<Long> participantIds = extractNonAttendanceParticipantIds(nonAttendances, studyId, specificWeek);

            if (hasCandidates(participantIds)) {
                log.info("결석 처리 정보 -> studyId = {}, weekly = {}, candidates = {}", studyId, specificWeek, participantIds);
                studyAttendanceRepository.updateParticipantStatus(studyId, specificWeek, participantIds, ABSENCE);
                memberRepository.applyScoreToAbsenceParticipant(participantIds);
            }
        });
    }

    private Set<Long> extractNonAttendanceParticipantIds(
            final List<StudyAttendance> nonAttendances,
            final Long studyId,
            final int week
    ) {
        return nonAttendances.stream()
                .filter(nonAttendance -> nonAttendance.getStudyId().equals(studyId) && nonAttendance.getWeek() == week)
                .map(StudyAttendance::getParticipantId)
                .collect(Collectors.toSet());
    }

    private boolean hasCandidates(final Set<Long> participantIds) {
        return !CollectionUtils.isEmpty(participantIds);
    }
}
