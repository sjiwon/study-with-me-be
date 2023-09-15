package com.kgu.studywithme.studyattendance.application.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.application.service.MemberReader;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.studyattendance.application.usecase.command.ManualAttendanceUseCase;
import com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus;
import com.kgu.studywithme.studyattendance.domain.model.StudyAttendance;
import com.kgu.studywithme.studyattendance.domain.repository.StudyAttendanceRepository;
import com.kgu.studywithme.studyattendance.exception.StudyAttendanceErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.NON_ATTENDANCE;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class ManualAttendanceService implements ManualAttendanceUseCase {
    private final StudyAttendanceRepository studyAttendanceRepository;
    private final MemberReader memberReader;

    @Override
    public void invoke(final Command command) {
        final StudyAttendance attendance = getParticipantAttendanceByWeek(
                command.studyId(),
                command.participantId(),
                command.week()
        );
        final AttendanceStatus previousStatus = attendance.getStatus();
        final AttendanceStatus currentStatus = command.attendanceStatus();

        attendance.updateAttendanceStatus(currentStatus);
        applyMemberScore(command.participantId(), previousStatus, currentStatus);
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
        final Member participant = memberReader.getById(participantId);

        if (previousStatus == NON_ATTENDANCE) {
            participant.applyScoreByAttendanceStatus(currentStatus);
        } else if (isStatusChanged(previousStatus, currentStatus)) {
            participant.applyScoreByAttendanceStatus(previousStatus, currentStatus);
        }
    }

    private boolean isStatusChanged(final AttendanceStatus previousStatus, final AttendanceStatus currentStatus) {
        return previousStatus != currentStatus;
    }
}
