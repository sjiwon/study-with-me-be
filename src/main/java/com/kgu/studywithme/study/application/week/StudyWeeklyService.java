package com.kgu.studywithme.study.application.week;

import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.application.MemberFindService;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.application.StudyFindService;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.study.domain.attendance.Attendance;
import com.kgu.studywithme.study.domain.attendance.AttendanceRepository;
import com.kgu.studywithme.study.domain.attendance.AttendanceStatus;
import com.kgu.studywithme.study.domain.week.Period;
import com.kgu.studywithme.study.domain.week.Week;
import com.kgu.studywithme.study.domain.week.WeekRepository;
import com.kgu.studywithme.study.domain.week.attachment.UploadAttachment;
import com.kgu.studywithme.study.domain.week.submit.Submit;
import com.kgu.studywithme.study.domain.week.submit.SubmitRepository;
import com.kgu.studywithme.study.domain.week.submit.UploadAssignment;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import com.kgu.studywithme.study.presentation.dto.request.StudyWeeklyRequest;
import com.kgu.studywithme.upload.application.FileUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

import static com.kgu.studywithme.study.domain.attendance.AttendanceStatus.*;

@Service
@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class StudyWeeklyService {
    private final StudyFindService studyFindService;
    private final StudyRepository studyRepository;
    private final MemberFindService memberFindService;
    private final WeekRepository weekRepository;
    private final AttendanceRepository attendanceRepository;
    private final SubmitRepository submitRepository;
    private final FileUploader uploader;

    @StudyWithMeWritableTransactional
    public void createWeek(
            final Long studyId,
            final StudyWeeklyRequest request
    ) {
        final Study study = studyFindService.findById(studyId);
        final List<UploadAttachment> attachments = createUploadAttachments(request.files());
        final int nextWeek = studyRepository.getNextWeek(study.getId());

        createWeekBasedOnAssignmentExistence(study, nextWeek, attachments, request);
        processAttendance(study, nextWeek);
    }

    private List<UploadAttachment> createUploadAttachments(final List<MultipartFile> files) {
        if (CollectionUtils.isEmpty(files)) {
            return List.of();
        }

        return files.stream()
                .map(file ->
                        UploadAttachment.of(
                                file.getOriginalFilename(),
                                uploader.uploadWeeklyAttachment(file)
                        )
                )
                .toList();
    }

    private void createWeekBasedOnAssignmentExistence(
            final Study study,
            final Integer week,
            final List<UploadAttachment> attachments,
            final StudyWeeklyRequest request
    ) {
        if (request.assignmentExists()) {
            study.createWeekWithAssignment(
                    request.title(),
                    request.content(),
                    week,
                    Period.of(request.startDate(), request.endDate()),
                    request.autoAttendance(),
                    attachments
            );
        } else {
            study.createWeek(
                    request.title(),
                    request.content(),
                    week,
                    Period.of(request.startDate(), request.endDate()),
                    attachments
            );
        }
    }

    private void processAttendance(
            final Study study,
            final Integer week
    ) {
        study.getApproveParticipants()
                .forEach(participant -> study.recordAttendance(participant, week, NON_ATTENDANCE));
    }

    @StudyWithMeWritableTransactional
    public void updateWeek(
            final Long studyId,
            final Integer week,
            final StudyWeeklyRequest request
    ) {
        final Week specificWeek = getSpecificWeek(studyId, week);
        final List<UploadAttachment> attachments = createUploadAttachments(request.files());

        specificWeek.update(
                request.title(),
                request.content(),
                Period.of(request.startDate(), request.endDate()),
                request.assignmentExists(),
                request.autoAttendance(),
                attachments
        );
    }

    @StudyWithMeWritableTransactional
    public void deleteWeek(
            final Long studyId,
            final Integer week
    ) {
        validateLatestWeek(studyId, week);
        studyRepository.deleteSpecificWeek(studyId, week);
    }

    private void validateLatestWeek(
            final Long studyId,
            final Integer week
    ) {
        if (!studyRepository.isLatestWeek(studyId, week)) {
            throw StudyWithMeException.type(StudyErrorCode.WEEK_IS_NOT_LATEST);
        }
    }

    @StudyWithMeWritableTransactional
    public void submitAssignment(
            final Long participantId,
            final Long studyId,
            final Integer week,
            final String type,
            final MultipartFile file,
            final String link
    ) {
        validateAssignmentSubmissionExists(file, link);

        final Week specificWeek = getSpecificWeek(studyId, week);
        final Member participant = memberFindService.findById(participantId);

        handleAssignmentSubmission(specificWeek, participant, type, file, link);
        processAttendanceBasedOnAutoAttendanceFlag(specificWeek, participant, studyId);
    }

    private void validateAssignmentSubmissionExists(
            final MultipartFile file,
            final String link
    ) {
        if (file == null && link == null) {
            throw StudyWithMeException.type(StudyErrorCode.MISSING_SUBMISSION);
        }

        if (file != null && link != null) {
            throw StudyWithMeException.type(StudyErrorCode.DUPLICATE_SUBMISSION);
        }
    }

    private Week getSpecificWeek(
            final Long studyId,
            final Integer week
    ) {
        return weekRepository.findByStudyIdAndWeek(studyId, week)
                .orElseThrow(() -> StudyWithMeException.type(StudyErrorCode.WEEK_NOT_FOUND));
    }

    private void handleAssignmentSubmission(
            final Week week,
            final Member participant,
            final String type,
            final MultipartFile file,
            final String link
    ) {
        final UploadAssignment uploadAssignment = createUpload(type, file, link);
        week.submitAssignment(participant, uploadAssignment);
    }

    private UploadAssignment createUpload(
            final String type,
            final MultipartFile file,
            final String link
    ) {
        return type.equals("file")
                ? UploadAssignment.withFile(file.getOriginalFilename(), uploader.uploadWeeklySubmit(file))
                : UploadAssignment.withLink(link);
    }

    private void processAttendanceBasedOnAutoAttendanceFlag(
            final Week week,
            final Member participant,
            final Long studyId
    ) {
        if (week.isAutoAttendance()) {
            final LocalDateTime now = LocalDateTime.now();
            final Period period = week.getPeriod();

            if (period.isDateWithInRange(now)) {
                applyAttendanceStatusAndMemberScore(participant, week.getWeek(), studyId);
            } else {
                applyLateStatusAndMemberScore(participant, week.getWeek(), studyId);
            }
        }
    }

    private void applyAttendanceStatusAndMemberScore(
            final Member participant,
            final int week,
            final Long studyId
    ) {
        final Attendance attendance = getParticipantAttendance(studyId, participant.getId(), week);
        attendance.updateAttendanceStatus(ATTENDANCE);
        participant.applyScoreByAttendanceStatus(ATTENDANCE);
    }

    private void applyLateStatusAndMemberScore(
            final Member participant,
            final int week,
            final Long studyId
    ) {
        final Attendance attendance = getParticipantAttendance(studyId, participant.getId(), week);
        final AttendanceStatus previousStatus = attendance.getStatus();

        attendance.updateAttendanceStatus(LATE);
        if (previousStatus == ABSENCE) { // 스케줄러에 의한 결석 처리
            participant.applyScoreByAttendanceStatus(ABSENCE, LATE);
        } else { // 미출결 상태
            participant.applyScoreByAttendanceStatus(LATE);
        }
    }

    private Attendance getParticipantAttendance(
            final Long studyId,
            final Long memberId,
            final Integer week
    ) {
        return attendanceRepository.findByStudyIdAndParticipantIdAndWeek(studyId, memberId, week)
                .orElseThrow(() -> StudyWithMeException.type(StudyErrorCode.ATTENDANCE_NOT_FOUND));
    }

    @StudyWithMeWritableTransactional
    public void editSubmittedAssignment(
            final Long participantId,
            final Long studyId,
            final Integer week,
            final String type,
            final MultipartFile file,
            final String link
    ) {
        validateAssignmentSubmissionExists(file, link);

        final Submit submit = getParticipantSubmit(participantId, week);
        final UploadAssignment newUploadAssignment = createUpload(type, file, link);
        submit.editUpload(newUploadAssignment);

        validateSubmitTimeAndApplyLateSubmissionPenalty(submit.getWeek(), submit.getParticipant(), studyId);
    }

    private Submit getParticipantSubmit(
            final Long participantId,
            final Integer week
    ) {
        return submitRepository.findByParticipantIdAndWeek(participantId, week)
                .orElseThrow(() -> StudyWithMeException.type(StudyErrorCode.SUBMIT_NOT_FOUND));
    }

    private void validateSubmitTimeAndApplyLateSubmissionPenalty(
            final Week week,
            final Member participant,
            final Long studyId
    ) {
        final LocalDateTime now = LocalDateTime.now();
        final Period period = week.getPeriod();

        if (week.isAutoAttendance() && !period.isDateWithInRange(now)) {
            Attendance attendance = getParticipantAttendance(studyId, participant.getId(), week.getWeek());

            if (attendance.isAttendanceStatus()) {
                attendance.updateAttendanceStatus(LATE);
                participant.applyScoreByAttendanceStatus(ATTENDANCE, LATE);
            }
        }
    }
}
