package com.kgu.studywithme.studyweekly.application.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.application.service.QueryMemberByIdService;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.studyattendance.domain.StudyAttendance;
import com.kgu.studywithme.studyattendance.domain.StudyAttendanceRepository;
import com.kgu.studywithme.studyattendance.exception.StudyAttendanceErrorCode;
import com.kgu.studywithme.studyweekly.application.usecase.command.EditSubmittedWeeklyAssignmentUseCase;
import com.kgu.studywithme.studyweekly.domain.Period;
import com.kgu.studywithme.studyweekly.domain.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.StudyWeeklyRepository;
import com.kgu.studywithme.studyweekly.domain.submit.StudyWeeklySubmit;
import com.kgu.studywithme.studyweekly.domain.submit.UploadAssignment;
import com.kgu.studywithme.studyweekly.exception.StudyWeeklyErrorCode;
import com.kgu.studywithme.upload.utils.FileUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.ATTENDANCE;
import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.LATE;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class EditSubmittedWeeklyAssignmentService implements EditSubmittedWeeklyAssignmentUseCase {
    private final StudyWeeklyRepository studyWeeklyRepository;
    private final StudyAttendanceRepository studyAttendanceRepository;
    private final QueryMemberByIdService queryMemberByIdService;
    private final FileUploader uploader;

    @Override
    public void editSubmittedWeeklyAssignment(final Command command) {
        validateAssignmentSubmissionExists(command.file(), command.link());

        final StudyWeeklySubmit submittedAssignment = getSubmittedAssignment(command.memberId(), command.studyId(), command.week());
        final UploadAssignment assignment = uploadAssignment(command.uploadType(), command.file(), command.link());
        submittedAssignment.editUpload(assignment);

        final Member member = queryMemberByIdService.findById(command.memberId());
        validateSubmitTimeAndApplyLateSubmissionPenalty(
                submittedAssignment.getStudyWeekly(),
                member,
                command.studyId()
        );
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

    private StudyWeeklySubmit getSubmittedAssignment(
            final Long memberId,
            final Long studyId,
            final int week
    ) {
        return studyWeeklyRepository.getSubmittedAssignment(memberId, studyId, week)
                .orElseThrow(() -> StudyWithMeException.type(StudyWeeklyErrorCode.SUBMITTED_ASSIGNMENT_NOT_FOUND));
    }

    private UploadAssignment uploadAssignment(
            final String type,
            final MultipartFile file,
            final String link
    ) {
        return type.equals("file")
                ? UploadAssignment.withFile(file.getOriginalFilename(), uploader.uploadWeeklySubmit(file))
                : UploadAssignment.withLink(link);
    }

    private void validateSubmitTimeAndApplyLateSubmissionPenalty(
            final StudyWeekly weekly,
            final Member member,
            final Long studyId
    ) {
        final LocalDateTime now = LocalDateTime.now();
        final Period period = weekly.getPeriod();

        if (weekly.isAutoAttendance() && !period.isDateWithInRange(now)) { // 수정 시간을 기준으로 제출 시간 업데이트
            final StudyAttendance attendance
                    = getParticipantAttendanceByWeek(studyId, member.getId(), weekly.getWeek());

            if (attendance.isAttendanceStatus()) {
                attendance.updateAttendanceStatus(LATE);
                member.applyScoreByAttendanceStatus(ATTENDANCE, LATE);
            }
        }
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
