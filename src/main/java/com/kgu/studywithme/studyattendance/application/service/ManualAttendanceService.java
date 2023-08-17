package com.kgu.studywithme.studyattendance.application.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.application.adapter.MemberReadAdapter;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.studyattendance.application.usecase.command.ManualAttendanceUseCase;
import com.kgu.studywithme.studyattendance.domain.AttendanceStatus;
import com.kgu.studywithme.studyattendance.domain.StudyAttendance;
import com.kgu.studywithme.studyattendance.domain.StudyAttendanceRepository;
import com.kgu.studywithme.studyattendance.exception.StudyAttendanceErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.NON_ATTENDANCE;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class ManualAttendanceService implements ManualAttendanceUseCase {
    private final StudyAttendanceRepository studyAttendanceRepository;
    private final MemberReadAdapter memberReadAdapter;

    @Override
    public void invoke(final Command command) {
        final StudyAttendance attendance = getParticipantAttendanceByWeek(
                command.studyId(),
                command.participantId(),
                command.week()
        );
        final AttendanceStatus previousStatus = attendance.getStatus();

        attendance.updateAttendanceStatus(command.attendanceStatus());
        applyMemberScore(command.participantId(), previousStatus, command.attendanceStatus());
    }

    private StudyAttendance getParticipantAttendanceByWeek(
            final Long studyId,
            final Long participantId,
            final Integer week
    ) {
        return studyAttendanceRepository.getParticipantAttendanceByWeek(studyId, participantId, week)
                .orElseThrow(() -> StudyWithMeException.type(StudyAttendanceErrorCode.ATTENDANCE_NOT_FOUND));
    }

    private void applyMemberScore(
            final Long participantId,
            final AttendanceStatus previousStatus,
            final AttendanceStatus currentStatus
    ) {
        final Member participant = memberReadAdapter.getById(participantId);

        if (previousStatus == NON_ATTENDANCE) {
            participant.applyScoreByAttendanceStatus(currentStatus);
        } else if (isStatusChanged(previousStatus, currentStatus)) {
            participant.applyScoreByAttendanceStatus(previousStatus, currentStatus);
        }
    }

    private boolean isStatusChanged(
            final AttendanceStatus previousStatus,
            final AttendanceStatus currentStatus
    ) {
        return previousStatus != currentStatus;
    }
}
