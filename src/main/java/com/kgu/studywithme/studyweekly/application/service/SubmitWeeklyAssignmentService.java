package com.kgu.studywithme.studyweekly.application.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.global.infrastructure.file.FileUploader;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.studyattendance.domain.AttendanceStatus;
import com.kgu.studywithme.studyattendance.domain.StudyAttendance;
import com.kgu.studywithme.studyattendance.domain.StudyAttendanceRepository;
import com.kgu.studywithme.studyattendance.exception.StudyAttendanceErrorCode;
import com.kgu.studywithme.studyparticipant.domain.StudyParticipantRepository;
import com.kgu.studywithme.studyparticipant.exception.StudyParticipantErrorCode;
import com.kgu.studywithme.studyweekly.application.usecase.command.SubmitWeeklyAssignmentUseCase;
import com.kgu.studywithme.studyweekly.domain.Period;
import com.kgu.studywithme.studyweekly.domain.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.StudyWeeklyRepository;
import com.kgu.studywithme.studyweekly.domain.submit.AssignmentSubmitType;
import com.kgu.studywithme.studyweekly.domain.submit.UploadAssignment;
import com.kgu.studywithme.studyweekly.exception.StudyWeeklyErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Set;

import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.ABSENCE;
import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.ATTENDANCE;
import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.LATE;
import static com.kgu.studywithme.studyweekly.domain.submit.AssignmentSubmitType.FILE;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class SubmitWeeklyAssignmentService implements SubmitWeeklyAssignmentUseCase {
    private final StudyWeeklyRepository studyWeeklyRepository;
    private final StudyParticipantRepository studyParticipantRepository;
    private final StudyAttendanceRepository studyAttendanceRepository;
    private final FileUploader uploader;

    @Override
    public void submitWeeklyAssignment(final Command command) {
        validateAssignmentSubmissionExists(command.file(), command.link());

        final StudyWeekly weekly = getSpecificWeekly(command.weeklyId());
        final Member member = getParticipant(command.studyId(), command.memberId());

        submitAssignment(weekly, member, command);
        processAttendanceBasedOnAutoAttendanceFlag(weekly, member, command.studyId());
    }

    private void validateAssignmentSubmissionExists(
            final MultipartFile file,
            final String link
    ) {
        if (file == null && link == null) {
            throw StudyWithMeException.type(StudyWeeklyErrorCode.MISSING_SUBMISSION);
        }

        if (file != null && link != null) {
            throw StudyWithMeException.type(StudyWeeklyErrorCode.DUPLICATE_SUBMISSION);
        }
    }

    private StudyWeekly getSpecificWeekly(final Long weeklyId) {
        return studyWeeklyRepository.findById(weeklyId)
                .orElseThrow(() -> StudyWithMeException.type(StudyWeeklyErrorCode.WEEKLY_NOT_FOUND));
    }

    private Member getParticipant(
            final Long studyId,
            final Long participantId
    ) {
        return studyParticipantRepository.findParticipant(studyId, participantId)
                .orElseThrow(() -> StudyWithMeException.type(StudyParticipantErrorCode.PARTICIPANT_NOT_FOUND));
    }

    private void submitAssignment(
            final StudyWeekly weekly,
            final Member member,
            final Command command
    ) {
        final UploadAssignment assignment = uploadAssignment(command.submitType(), command.file(), command.link());
        weekly.submitAssignment(member.getId(), assignment);
    }

    private UploadAssignment uploadAssignment(
            final AssignmentSubmitType submitType,
            final MultipartFile file,
            final String link
    ) {
        return submitType == FILE
                ? UploadAssignment.withFile(file.getOriginalFilename(), uploader.uploadWeeklySubmit(file))
                : UploadAssignment.withLink(link);
    }

    private void processAttendanceBasedOnAutoAttendanceFlag(
            final StudyWeekly weekly,
            final Member member,
            final Long studyId
    ) {
        if (weekly.isAutoAttendance()) {
            final LocalDateTime now = LocalDateTime.now();
            final Period period = weekly.getPeriod();

            if (period.isDateWithInRange(now)) {
                applyAttendanceStatusAndMemberScore(member, weekly.getWeek(), studyId);
            } else {
                applyLateStatusAndMemberScore(member, weekly.getWeek(), studyId);
            }
        }
    }

    private void applyAttendanceStatusAndMemberScore(
            final Member member,
            final int week,
            final Long studyId
    ) {
        member.applyScoreByAttendanceStatus(ATTENDANCE);
        studyAttendanceRepository.updateParticipantStatus(studyId, week, Set.of(member.getId()), ATTENDANCE);
    }

    private void applyLateStatusAndMemberScore(
            final Member member,
            final int week,
            final Long studyId
    ) {
        final StudyAttendance attendance = getParticipantAttendanceByWeek(studyId, member.getId(), week);
        final AttendanceStatus status = attendance.getStatus();

        if (status == ABSENCE) { // Scheduler에 의한 자동 결석 처리
            member.applyScoreByAttendanceStatus(ABSENCE, LATE);
        } else { // 미출결 (NON_ATTENDANCE)
            member.applyScoreByAttendanceStatus(LATE);
        }

        studyAttendanceRepository.updateParticipantStatus(studyId, week, Set.of(member.getId()), LATE);
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
