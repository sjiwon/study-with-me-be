package com.kgu.studywithme.study.application.attendance;

import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.application.service.QueryMemberByIdService;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.attendance.Attendance;
import com.kgu.studywithme.study.domain.attendance.AttendanceRepository;
import com.kgu.studywithme.study.domain.attendance.AttendanceStatus;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.kgu.studywithme.study.domain.attendance.AttendanceStatus.NON_ATTENDANCE;

@Service
@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final QueryMemberByIdService queryMemberByIdService;

    @StudyWithMeWritableTransactional
    public void manualCheckAttendance(
            final Long studyId,
            final Long memberId,
            final Integer week,
            final AttendanceStatus status
    ) {
        validateUpdateStatusIsNotNonAttendance(status);

        final Attendance attendance = getParticipantAttendance(studyId, memberId, week);
        final AttendanceStatus previousStatus = attendance.getStatus();

        attendance.updateAttendanceStatus(status);
        applyMemberScore(memberId, previousStatus, status);
    }

    private Attendance getParticipantAttendance(
            final Long studyId,
            final Long memberId,
            final Integer week
    ) {
        return attendanceRepository.findByStudyIdAndParticipantIdAndWeek(studyId, memberId, week)
                .orElseThrow(() -> StudyWithMeException.type(StudyErrorCode.ATTENDANCE_NOT_FOUND));
    }

    private void validateUpdateStatusIsNotNonAttendance(final AttendanceStatus status) {
        if (status == NON_ATTENDANCE) {
            throw StudyWithMeException.type(StudyErrorCode.CANNOT_UPDATE_TO_NON_ATTENDANCE);
        }
    }

    private void applyMemberScore(
            final Long memberId,
            final AttendanceStatus previous,
            final AttendanceStatus current
    ) {
        final Member member = queryMemberByIdService.findById(memberId);

        if (previous == NON_ATTENDANCE) {
            member.applyScoreByAttendanceStatus(current);
        } else if (isStatusChanged(previous, current)) {
            member.applyScoreByAttendanceStatus(previous, current);
        }
    }

    private boolean isStatusChanged(
            final AttendanceStatus previous,
            final AttendanceStatus current
    ) {
        return previous != current;
    }
}
