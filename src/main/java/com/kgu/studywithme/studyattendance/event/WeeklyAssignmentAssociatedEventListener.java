package com.kgu.studywithme.studyattendance.event;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.studyattendance.domain.AttendanceStatus;
import com.kgu.studywithme.studyattendance.domain.StudyAttendance;
import com.kgu.studywithme.studyattendance.domain.StudyAttendanceRepository;
import com.kgu.studywithme.studyattendance.exception.StudyAttendanceErrorCode;
import com.kgu.studywithme.studyparticipant.application.adapter.ParticipateMemberReadAdapter;
import com.kgu.studywithme.studyweekly.domain.Period;
import com.kgu.studywithme.studyweekly.domain.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.StudyWeeklyRepository;
import com.kgu.studywithme.studyweekly.event.AssignmentEditedEvent;
import com.kgu.studywithme.studyweekly.event.AssignmentSubmittedEvent;
import com.kgu.studywithme.studyweekly.exception.StudyWeeklyErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.ABSENCE;
import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.ATTENDANCE;
import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.LATE;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeeklyAssignmentAssociatedEventListener {
    private final StudyWeeklyRepository studyWeeklyRepository;
    private final ParticipateMemberReadAdapter participateMemberReadAdapter;
    private final StudyAttendanceRepository studyAttendanceRepository;

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void submitAssignmentAndApplyAttendanceStatusAndMemberScore(final AssignmentSubmittedEvent event) {
        log.info("과제 제출에 따른 출석 & 사용자 포인트 업데이트 이벤트 -> {}", event);

        final StudyWeekly weekly = getSpecificWeekly(event.weeklyId());
        final Member participant = participateMemberReadAdapter.getParticipant(event.studyId(), event.participantId());

        if (weekly.isAutoAttendance()) {
            final StudyAttendance attendance
                    = getParticipantAttendanceByWeek(event.studyId(), participant.getId(), weekly.getWeek());
            final LocalDateTime now = LocalDateTime.now();
            final Period period = weekly.getPeriod();

            if (period.isDateWithInRange(now)) {
                attendance.updateAttendanceStatus(ATTENDANCE);
                participant.applyScoreByAttendanceStatus(ATTENDANCE);
            } else {
                attendance.updateAttendanceStatus(LATE);
                applyLateScore(participant, attendance.getStatus());
            }
        }
    }

    private void applyLateScore(
            final Member participant,
            final AttendanceStatus status
    ) {
        if (status == ABSENCE) { // Scheduler에 의한 자동 결석 처리
            participant.applyScoreByAttendanceStatus(ABSENCE, LATE);
        } else { // 미출결 (NON_ATTENDANCE)
            participant.applyScoreByAttendanceStatus(LATE);
        }
    }

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void editAssignmentAndApplyAttendanceStatusAndMemberScore(final AssignmentEditedEvent event) {
        log.info("제출한 과제 수정에 따른 출석 & 사용자 포인트 업데이트 이벤트 -> {}", event);

        final StudyWeekly weekly = getSpecificWeekly(event.weeklyId());
        final Member participant = participateMemberReadAdapter.getParticipant(event.studyId(), event.participantId());

        final LocalDateTime now = LocalDateTime.now();
        final Period period = weekly.getPeriod();
        if (weekly.isAutoAttendance() && !period.isDateWithInRange(now)) { // 수정 시간을 기준으로 제출 시간 업데이트
            final StudyAttendance attendance
                    = getParticipantAttendanceByWeek(event.studyId(), participant.getId(), weekly.getWeek());

            if (attendance.isAttendanceStatus()) {
                attendance.updateAttendanceStatus(LATE);
                participant.applyScoreByAttendanceStatus(ATTENDANCE, LATE);
            }
        }
    }

    private StudyWeekly getSpecificWeekly(final Long weeklyId) {
        return studyWeeklyRepository.findById(weeklyId)
                .orElseThrow(() -> StudyWithMeException.type(StudyWeeklyErrorCode.WEEKLY_NOT_FOUND));
    }

    private StudyAttendance getParticipantAttendanceByWeek(
            final Long studyId,
            final Long participantId,
            final int week
    ) {
        return studyAttendanceRepository.getParticipantAttendanceByWeek(studyId, participantId, week)
                .orElseThrow(() -> StudyWithMeException.type(StudyAttendanceErrorCode.ATTENDANCE_NOT_FOUND));
    }
}
