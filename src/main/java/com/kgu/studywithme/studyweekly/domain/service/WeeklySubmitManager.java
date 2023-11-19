package com.kgu.studywithme.studyweekly.domain.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus;
import com.kgu.studywithme.studyattendance.domain.model.StudyAttendance;
import com.kgu.studywithme.studyattendance.domain.repository.StudyAttendanceRepository;
import com.kgu.studywithme.studyparticipant.domain.repository.query.ParticipateMemberReader;
import com.kgu.studywithme.studyweekly.domain.model.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.model.StudyWeeklySubmit;
import com.kgu.studywithme.studyweekly.domain.model.UploadAssignment;
import com.kgu.studywithme.studyweekly.domain.repository.StudyWeeklyRepository;
import com.kgu.studywithme.studyweekly.domain.repository.StudyWeeklySubmitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.ABSENCE;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.ATTENDANCE;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.LATE;

@Component
@RequiredArgsConstructor
public class WeeklySubmitManager {
    private final StudyWeeklyRepository studyWeeklyRepository;
    private final ParticipateMemberReader participateMemberReader;
    private final StudyWeeklySubmitRepository studyWeeklySubmitRepository;
    private final StudyAttendanceRepository studyAttendanceRepository;

    @StudyWithMeWritableTransactional
    public void submitAssignment(
            final Long memberId,
            final Long studyId,
            final Long weeklyId,
            final UploadAssignment assignment
    ) {
        final StudyWeekly weekly = studyWeeklyRepository.getById(weeklyId);
        final Member participant = participateMemberReader.getParticipant(studyId, memberId);
        weekly.submitAssignment(participant, assignment);

        applyAttendanceStatusAndMemberScoreViaSubmit(weekly, participant);
    }

    private void applyAttendanceStatusAndMemberScoreViaSubmit(final StudyWeekly weekly, final Member participant) {
        if (weekly.isAutoAttendance()) {
            final StudyAttendance attendance
                    = studyAttendanceRepository.getParticipantAttendanceByWeek(weekly.getStudy().getId(), participant.getId(), weekly.getWeek());
            final LocalDateTime now = LocalDateTime.now();

            if (weekly.isSubmissionPeriodInRange(now)) {
                participant.applyScoreByAttendanceStatus(ATTENDANCE);
                attendance.updateAttendanceStatus(ATTENDANCE);
            }

            if (weekly.isSubmissionPeriodPassed(now)) {
                applyLateScore(participant, attendance.getStatus());
                attendance.updateAttendanceStatus(LATE);
            }
        }
    }

    private void applyLateScore(final Member participant, final AttendanceStatus status) {
        if (status == ABSENCE) { // Scheduler에 의한 자동 결석 처리
            participant.applyScoreByAttendanceStatus(ABSENCE, LATE);
        } else { // 미출결 (NON_ATTENDANCE)
            participant.applyScoreByAttendanceStatus(LATE);
        }
    }

    @StudyWithMeWritableTransactional
    public void editSubmittedAssignment(
            final Long memberId,
            final Long studyId,
            final Long weeklyId,
            final UploadAssignment assignment
    ) {
        final StudyWeeklySubmit submittedAssignment = studyWeeklySubmitRepository.getSubmittedAssignment(weeklyId, memberId);
        submittedAssignment.editUpload(assignment);

        applyAttendanceStatusAndMemberScoreViaEdit(submittedAssignment.getWeekly(), studyId, memberId);
    }

    private void applyAttendanceStatusAndMemberScoreViaEdit(
            final StudyWeekly weekly,
            final Long studyId,
            final Long participantId
    ) {
        final Member participant = participateMemberReader.getParticipant(studyId, participantId);
        final LocalDateTime now = LocalDateTime.now();

        if (weekly.isAutoAttendance() && weekly.isSubmissionPeriodPassed(now)) { // 수정 시간을 기준으로 제출 시간 업데이트
            final StudyAttendance attendance
                    = studyAttendanceRepository.getParticipantAttendanceByWeek(weekly.getStudy().getId(), participant.getId(), weekly.getWeek());

            if (attendance.isAttendanceStatus()) {
                participant.applyScoreByAttendanceStatus(ATTENDANCE, LATE);
                attendance.updateAttendanceStatus(LATE);
            }
        }
    }
}
